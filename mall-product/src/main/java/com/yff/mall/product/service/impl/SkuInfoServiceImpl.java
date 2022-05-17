package com.yff.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.feign.SeckillSkuRedisTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.common.utils.R;
import com.yff.mall.product.dao.SkuInfoDao;
import com.yff.mall.product.entity.SkuImagesEntity;
import com.yff.mall.product.entity.SkuInfoEntity;
import com.yff.mall.product.entity.SpuInfoDescEntity;
import com.yff.mall.product.feign.SeckillFeignService;
import com.yff.mall.product.service.*;
import com.yff.mall.product.vo.SaleAttrVo;
import com.yff.mall.product.vo.SkuInfoRespVo;
import com.yff.mall.product.vo.SkuItemVo;
import com.yff.mall.product.vo.SpuItemBaseAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<SkuInfoEntity>()
                .eq(!"0".equals(brandId), "brand_id", brandId)
                .eq(!"0".equals(catelogId), "catalog_id", catelogId)
                .between(!"0".equals(min) || !"0".equals(max), "price", min, max);

        if (StringUtils.hasText(key)) {
            wrapper.and(data -> data.like("sku_title", key)
                            .or().like("sku_subtitle", key))
                    .or().like("price", key)
                    .or().like("sale_count", key);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoRespVo> listAll() {
        List<SkuInfoRespVo> list = this.list().stream().map(data -> {
            SkuInfoRespVo vo = new SkuInfoRespVo();
            BeanUtils.copyProperties(data, vo);
            return vo;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo vo = new SkuItemVo();

        //1，sku基本信息 pms_sku_info
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            vo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        //2,与1独立，可以再开一个异步运行
        //2，sku图片信息 pms_sku_images
        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            //2，sku图片信息 pms_sku_images
            List<SkuImagesEntity> skuImagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>()
                    .eq("sku_id", skuId).orderByDesc("default_img", "1"));
            vo.setSkuImagesEntities(skuImagesEntityList);
        }, executor);

        //3,4,5需要等待1完成才进行
        //3，spu销售属性
        CompletableFuture<Void> saleAttrsFuture = skuInfoFuture.thenAcceptAsync(res -> {
            //3，spu销售属性
            List<SaleAttrVo> saleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            vo.setSaleAttrVos(saleAttrsVos);
        }, executor);

        //4，spu介绍
        CompletableFuture<Void> spuInfoDescFuture = skuInfoFuture.thenAcceptAsync(res -> {
            //4，spu介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            vo.setSpuInfoDescEntity(spuInfoDescEntity);
        }, executor);

        //5，spu的规格参数
        CompletableFuture<Void> groupAttrFuture = skuInfoFuture.thenAcceptAsync(res -> {
            Long spuId = res.getSpuId();
            Long catalogId = res.getCatalogId();
            //5，spu的规格参数
            List<SpuItemBaseAttrVo> groupAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
            vo.setGroupAttrVos(groupAttrVos);
        }, executor);

        //6，秒杀预告信息
        CompletableFuture<Void> seckillSkuFuture = skuInfoFuture.thenAcceptAsync(res -> {
            R r = seckillFeignService.getSkuSeckillInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuRedisTo seckillSkuRedisTo = r.getData(new TypeReference<SeckillSkuRedisTo>() {
                });
                vo.setSeckillSkuRedisTo(seckillSkuRedisTo);
            }
        }, executor);

        CompletableFuture.allOf(skuImagesFuture, saleAttrsFuture, spuInfoDescFuture, groupAttrFuture, seckillSkuFuture).get();
        return vo;
    }

}
