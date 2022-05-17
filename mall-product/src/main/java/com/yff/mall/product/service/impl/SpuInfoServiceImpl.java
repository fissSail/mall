package com.yff.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.constant.ProductConstant;
import com.yff.common.to.SkuReductionTo;
import com.yff.common.to.SpuBoundsTo;
import com.yff.common.to.feign.WareSkuTo;
import com.yff.common.to.es.AttrValue;
import com.yff.common.to.es.SkuEsTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.common.utils.R;
import com.yff.mall.product.dao.SpuInfoDao;
import com.yff.mall.product.entity.*;
import com.yff.mall.product.feign.CouponFeignService;
import com.yff.mall.product.feign.SearchFeignService;
import com.yff.mall.product.feign.WareFeignService;
import com.yff.mall.product.service.*;
import com.yff.mall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String status = (String) params.get("status");
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<SpuInfoEntity>()
                .eq(StringUtils.hasText(status), "publish_status", status)
                .eq(StringUtils.hasText(brandId) && !"0".equals(brandId), "brand_id", brandId)
                .eq(StringUtils.hasText(catelogId) && !"0".equals(catelogId), "catalog_id", catelogId);

        if (StringUtils.hasText(key)) {
            wrapper.and(data ->
                    data.like("spu_name", key)
                            .or().like("spu_description", key)
                            .or().like("weight", key)
            );
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params), wrapper);

        List<SpuInfoRespVo> list = page.getRecords().stream().map(data -> {
            SpuInfoRespVo vo = new SpuInfoRespVo();
            BeanUtils.copyProperties(data, vo);
            CategoryEntity category = categoryService.getById(data.getCatalogId());
            vo.setCategoryName(category.getName());
            return vo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);

        pageUtils.setList(list);
        return pageUtils;
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //保存spu基本信息pms_sku_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.save(spuInfo);

        Long spuId = spuInfo.getId();

        //保存spu描述图片pms_spu_info_desc
        List<String> decriptList = vo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuId);
        spuInfoDesc.setDecript(String.join(",", decriptList));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        //保存spu图片集pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveSpuImages(images, spuId);

        //保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveProductAttrValueList(baseAttrs, spuId);

        //保存spu积分信息sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveBounds(spuBoundsTo);
        if (r.getCode() == 0) {
            log.info("saveBounds远程调用成功");
        }

        //保存spu对应所有sku信息
        //保存sku基本信息pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (CollectionUtils.isNotEmpty(skus)) {
            skus.forEach(data -> {
                List<Images> skusImages = data.getImages();

                String defaultImg = "";
                for (Images skusImage : skusImages) {
                    if (skusImage.getDefaultImg() == 1) {
                        defaultImg = skusImage.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfo = new SkuInfoEntity();
                BeanUtils.copyProperties(data, skuInfo);
                skuInfo.setSpuId(spuId);
                skuInfo.setBrandId(vo.getBrandId());
                skuInfo.setCatalogId(vo.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfo);

                Long skuId = skuInfo.getSkuId();
                //保存sku图片集pms_sku_images
                skuImagesService.saveSkuImagesList(skusImages, skuId);

                //保存sku销售属性pms_sku_sale_attr_value
                List<Attr> attr = data.getAttr();
                skuSaleAttrValueService.saveSkuSaleAttrValueList(attr, skuId);

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(data, skuReductionTo);
                skuReductionTo.setSkuId(skuId);

                //TODO
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {

                }

                R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode() == 0) {
                    log.info("saveSkuReduction远程调用成功");
                }
            });
        }
    }

    @Override
    @Transactional
    public void updatePublishStatusAndSaveEs(Long spuId) {
        //查询sku信息
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //查询当前商品规格参数信息
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.queryProductAttrValueBySpuId(spuId);
        //过滤出可以被检索的规格参数id set
        Set<Long> attrIdSet = attrService.listByIds(productAttrValueEntityList
                        .stream().map(data -> data.getAttrId()).collect(Collectors.toList()))
                .stream().filter(data -> data.getSearchType() == 1)
                .map(data -> data.getAttrId()).collect(Collectors.toSet());
        //组装可检索规格参数
        List<AttrValue> attrs = productAttrValueEntityList.stream()
                .filter(data -> attrIdSet.contains(data.getAttrId())).map(data -> {
                    AttrValue attr = new AttrValue();
                    BeanUtils.copyProperties(data, attr);
                    return attr;
                }).collect(Collectors.toList());

        //获取所有skuid
        List<Long> skuIdList = skuInfoEntityList.stream().map(data -> data.getSkuId()).collect(Collectors.toList());
        //访问库存系统-查询库存
        Map<Long, Boolean> wareSkuMap = null;
        try {
            R r = wareFeignService.getSkuStock(skuIdList);
            if (r.getCode() == 0) {
                log.info("getWareSkuInfo远程调用成功");
                //收集流 将List<WareSkuTo>收集成map
                wareSkuMap = r.getData(new TypeReference<List<WareSkuTo>>() {
                        })
                        .stream()
                        .collect(Collectors.toMap(WareSkuTo::getSkuId, WareSkuTo::getHasStock));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<Long, Boolean> finalWareSkuMap = wareSkuMap;
        List<SkuEsTo> skuEsTos = skuInfoEntityList.stream().map(data -> {
            SkuEsTo skuEsTo = new SkuEsTo();
            //复制sku信息
            BeanUtils.copyProperties(data, skuEsTo);
            skuEsTo.setSkuImg(data.getSkuDefaultImg());
            skuEsTo.setSkuPrice(data.getPrice());

            //查询是否有库存
            skuEsTo.setHasStock(CollectionUtils.isEmpty(finalWareSkuMap) ? true : finalWareSkuMap.get(data.getSkuId()));

            //查询品牌
            BrandEntity brandEntity = brandService.getById(data.getBrandId());
            skuEsTo.setBrandName(brandEntity.getName());
            skuEsTo.setBrandImg(brandEntity.getLogo());

            //查询分类
            CategoryEntity categoryEntity = categoryService.getById(data.getCatalogId());
            skuEsTo.setCatalogName(categoryEntity.getName());

            //设置检索属性
            skuEsTo.setAttrs(attrs);
            //热度
            skuEsTo.setHotScore(0L);
            return skuEsTo;
        }).collect(Collectors.toList());

        //存入elasticSearch
        R r = searchFeignService.saveProduct(skuEsTos);
        if (r.getCode() == 0) {
            log.info("上架成功");
            //修改上架状态
            SpuInfoEntity updateSpuInfoEntity = new SpuInfoEntity();
            updateSpuInfoEntity.setPublishStatus(ProductConstant.PublishStatusEnum.PUTAWAY_SPU.getCode());
            updateSpuInfoEntity.setUpdateTime(new Date());
            this.update(updateSpuInfoEntity, new QueryWrapper<SpuInfoEntity>().eq("id", spuId));
        }

    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        Long spuId = skuInfo.getSpuId();
        SpuInfoEntity spuInfo = this.getById(spuId);
        return spuInfo;
    }

}
