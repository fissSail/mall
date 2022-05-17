package com.yff.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.CategoryDao;
import com.yff.mall.product.entity.CategoryEntity;
import com.yff.mall.product.service.CategoryBrandRelationService;
import com.yff.mall.product.service.CategoryService;
import com.yff.mall.product.vo.CategoryRespVo;
import com.yff.mall.product.vo.CategoryVo;
import lombok.Data;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Data
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        String catId = (String) params.get("catId");

        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>().like("name", name)
                        .and(StringUtils.hasText(catId),
                                wrapper -> wrapper.eq("cat_id", catId).or().eq("parent_cid", catId))
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryVo> queryPageByTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        List<CategoryVo> collect = categoryEntities.stream().map(data -> {
            CategoryVo vo = new CategoryVo();
            BeanUtils.copyProperties(data, vo);
            return vo;
        }).collect(Collectors.toList());

        return collect
                .stream()
                .filter(data -> data.getParentCid() == 0)
                .map(data -> {
                    data.setChildren(getCategoryVoChildren(data, collect));
                    return data;
                })
                .sorted((v1, v2) -> (Objects.isNull(v1.getSort()) ? 0 : v1.getSort()) - (Objects.isNull(v2.getSort()) ? 0 : v2.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public Long[] getCatelogPath(Long catId) {
        List<Long> longList = new ArrayList<>();
        List<Long> catelog = getCatelog(catId, longList);
        Collections.reverse(catelog);
        return catelog.toArray(new Long[catelog.size()]);
    }


    /*@Caching(evict={
            @CacheEvict(value = "category", key = "'getCategoryStair'"),  //缓存失效模式
            @CacheEvict(value = "category", key = "'getCategoryJson'"),  //缓存失效模式
    })*/
    @CacheEvict(value = "category",allEntries = true)
    @Override
    @Transactional
    public void updateRelevanceById(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.hasText(category.getName())) {
            categoryBrandRelationService.updateByCategory(category.getCatId(), category.getName());
        }
    }

    //每一个需要缓存的数据指定缓存名称
    @Cacheable(value = "category", key = "#root.method.name") //代表当前方法的结果需要缓存，如果缓存中有方法不调用，如果缓存中没有，会调用方法并放入缓存
    @Override
    public List<CategoryEntity> getCategoryStair(int catLevel) {

        List<CategoryEntity> categoryList = this.list(new QueryWrapper<CategoryEntity>()
                .eq(Objects.nonNull(catLevel), "cat_level", catLevel));
        return categoryList;
    }

    @Cacheable(value = "category",key = "#root.methodName",sync = true)
    @Override
    public Map<Long, List<CategoryRespVo>> getCategoryJson() {
        return this.getCategoryByDB();
    }

    public Map<Long, List<CategoryRespVo>> getCategoryJson2() {
        /**
         * 1，空结果缓存
         * 2，缓存加随机过期时间
         * 3，加锁
         */

        String categoryJson = redisTemplate.opsForValue().get("categoryJson");

        if (!StringUtils.hasText(categoryJson)) {

            Map<Long, List<CategoryRespVo>> map = this.getCategoryJsonByDBUseRedisson();

            return map;
        }
        Map<Long, List<CategoryRespVo>> map = JSON.parseObject(categoryJson, new TypeReference<Map<Long, List<CategoryRespVo>>>() {
        });

        return map;
    }

    /**
     * 数据库查询三级分类使用Synchronized
     *
     * @return
     */
    private Map<Long, List<CategoryRespVo>> getCategoryJsonByDBUseSynchronized() {
        //synchronized (this) ，可以锁住拥有同一把锁的其他线程，由于springboot所有组件在容器中都是单例对象，所以可以用this
        //synchronized，lock都是本地锁，在分布式情况下，要锁住所有，必须使用分布式锁
        synchronized (this) {
            return this.getCategoryByDBAndJudgeRedis();
        }
    }

    /**
     * 数据库查询三级分类使用redis锁setnx
     *
     * @return
     */
    private Map<Long, List<CategoryRespVo>> getCategoryJsonByDBUseRedisLock() {
        /**
         * 使用setnx乐观锁，谁先设置key-value，后面线程发现key存在就不设置
         * 设置过期时间30秒，防止各种原因而导致未删除key（未释放锁）（死锁），设置过期时间要与setnx一起设置，保证原子性
         */
        String uuid = UUID.randomUUID().toString();
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);

        if (isLock) {
            //获得到锁，查询数据库
            Map<Long, List<CategoryRespVo>> map;
            try {
                map = this.getCategoryByDBAndJudgeRedis();
            } finally {
                /**
                 * 释放锁
                 * 1，假如查询数据库需要30秒，锁过期时间为10秒，查询完数据库后，锁已经自动释放，
                 * 后一个线程进行继续执行数据库操作，同样需要30秒查询，锁也提前释放，后面线程以此类推，
                 * 这时第一个线程刚好执行删除锁，那么它就释放了所有获得到锁了线程
                 * 解决：占锁时，使用一个唯一的id，释放锁时，先判断是否有指定的值，有则删
                 */
                /*String lockValue = redisTemplate.opsForValue().get("lock");
                if(uuid.equals(lockValue)){
                    redisTemplate.delete("lock");
                }*/

                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                //删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return map;
        }

        try {
            TimeUnit.SECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //没抢到锁一直重试
        return getCategoryJsonByDBUseRedisLock();
    }

    /**
     * 数据库查询三级分类使用redisson
     *
     * @return
     */
    private Map<Long, List<CategoryRespVo>> getCategoryJsonByDBUseRedisson() {

        RLock lock = redissonClient.getLock("ctegoryJson-lock");

        lock.lock(10, TimeUnit.SECONDS);
        //获得到锁，查询数据库
        Map<Long, List<CategoryRespVo>> map;
        try {
            map = this.getCategoryByDBAndJudgeRedis();
        } finally {
            lock.unlock();
        }
        return map;
    }

    /**
     * 数据库查询三级分类并判断存入缓存
     *
     * @return
     */
    private Map<Long, List<CategoryRespVo>> getCategoryByDBAndJudgeRedis() {
        //查询数据库前再查询缓存是否有数据
        String categoryJson = redisTemplate.opsForValue().get("categoryJson");
        //有则直接返回
        if (StringUtils.hasText(categoryJson)) {
            Map<Long, List<CategoryRespVo>> map = JSON.parseObject(categoryJson, new TypeReference<Map<Long, List<CategoryRespVo>>>() {
            });

            return map;
        }

        Map<Long, List<CategoryRespVo>> map = getCategoryByDB();
        //查询到数据，存入缓存
        redisTemplate.opsForValue().set("categoryJson", JSON.toJSONString(map), ThreadLocalRandom.current().nextInt(1, 10), TimeUnit.MINUTES);

        return map;
    }

    /**
     *  数据库查询三级分类
     * @return
     */
    private Map<Long, List<CategoryRespVo>> getCategoryByDB() {
        List<CategoryEntity> list = this.list();

        Map<Long, List<CategoryRespVo>> map = list.stream()
                .filter(data -> data.getParentCid() == 0)
                .map(data -> {
                    CategoryRespVo vo = new CategoryRespVo();
                    vo.setCatalogId(data.getCatId());
                    vo.setParentId(data.getParentCid());
                    vo.setName(data.getName());
                    vo.setChildrenList(getCategoryChildren(vo, list));
                    return vo;
                }).collect(Collectors.toMap(CategoryRespVo::getCatalogId, CategoryRespVo::getChildrenList));
        return map;
    }

    /**
     * 递归查询
     *
     * @param vo
     * @param list
     * @return
     */
    private List<CategoryRespVo> getCategoryChildren(CategoryRespVo vo, List<CategoryEntity> list) {
        List<CategoryRespVo> categoryRespVos = list.stream()
                .filter(data -> data.getParentCid().equals(vo.getCatalogId()))
                .map(data -> {
                    CategoryRespVo respVo = new CategoryRespVo();
                    respVo.setCatalogId(data.getCatId());
                    respVo.setParentId(data.getParentCid());
                    respVo.setName(data.getName());
                    respVo.setChildrenList(getCategoryChildren(respVo, list));
                    return respVo;
                }).collect(Collectors.toList());
        return categoryRespVos;
    }

    /**
     * 递归查询3级分类的父子关系节点id
     *
     * @param catelogId
     * @param longList
     * @return
     */
    private List<Long> getCatelog(Long catelogId, List<Long> longList) {
        longList.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            getCatelog(byId.getParentCid(), longList);
        }
        return longList;
    }

    /**
     * 递归组装3级树
     *
     * @param vo
     * @param categoryVoList
     * @return
     */
    private List<CategoryVo> getCategoryVoChildren(CategoryVo vo, List<CategoryVo> categoryVoList) {
        return categoryVoList
                .stream()
                .filter(data -> data.getParentCid().equals(vo.getCatId()))
                .map(data -> {
                    data.setChildren(getCategoryVoChildren(data, categoryVoList));
                    return data;
                })
                .sorted((v1, v2) -> (Objects.isNull(v1.getSort()) ? 0 : v1.getSort()) - (Objects.isNull(v2.getSort()) ? 0 : v2.getSort()))
                .collect(Collectors.toList());
    }

}
