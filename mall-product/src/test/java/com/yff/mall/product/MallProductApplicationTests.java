package com.yff.mall.product;


import com.yff.mall.product.dao.AttrGroupDao;
import com.yff.mall.product.dao.SkuSaleAttrValueDao;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.service.BrandService;
import com.yff.mall.product.vo.SpuItemBaseAttrVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AttrGroupDao dao;
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setName("华为");
        brandService.save(brandEntity);
    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("key","20220111");
        ops.get("key");

    }

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);

    }

    @Test
    public void testDao(){
        List<SpuItemBaseAttrVo> attrGroupWithAttrsBySpuId = dao.getAttrGroupWithAttrsBySpuId(13L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }
    @Test
    public void testSkuSaleAttrValueDao(){
        //List<Attr> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(13L);
        //System.out.println(saleAttrsBySpuId);
    }

}
