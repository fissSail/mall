package com.yff.mall.product.web;

import com.yff.common.utils.R;
import com.yff.mall.product.entity.CategoryEntity;
import com.yff.mall.product.service.CategoryService;
import com.yff.mall.product.vo.CategoryRespVo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.web
 * @Description
 * @date 2022/1/6 10:48
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 商城首页
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        //一级分类
        List<CategoryEntity> categories = categoryService.getCategoryStair(1);
        model.addAttribute("categories", categories);
        return "index";
    }

    /**
     * 3级分类
     *
     * @return
     */
    @GetMapping("index/categoryJson")
    @ResponseBody
    public R getCategoryJson() {
        Map<Long, List<CategoryRespVo>> map = categoryService.getCategoryJson();

        return R.ok().put("data", map);
    }


    @GetMapping("hello")
    @ResponseBody
    public String hello() {
        RLock lock = redissonClient.getLock("lock");

        lock.lock();

        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("获得到锁-----" + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("释放锁------" + Thread.currentThread().getName());
        }

        return "hello";
    }

    @GetMapping("read")
    @ResponseBody
    public String read() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("lock");

        RLock rLock = lock.readLock();

        rLock.lock();
        try {
            System.out.println("获得到读锁-----" + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("释放读锁------" + Thread.currentThread().getName());
        }

        return "read";
    }

    @GetMapping("write")
    @ResponseBody
    public String write() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("lock");

        RLock writeLock = lock.writeLock();

        writeLock.lock();
        try {
            System.out.println("获得到写锁-----" + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
            System.out.println("释放写锁------" + Thread.currentThread().getName());
        }

        return "write";
    }

    @GetMapping("wait")
    @ResponseBody
    public String waitCat() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        //semaphore.acquire(2);
        boolean b = semaphore.tryAcquire(5);

        return "ok=>"+b;
    }

    @GetMapping("go")
    @ResponseBody
    public String goCat() {
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        semaphore.release(5);
        return "ok";
    }

    @GetMapping("close")
    @ResponseBody
    public String closeDoor() throws InterruptedException {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("countDownLatch");
        boolean b = countDownLatch.trySetCount(10);
        countDownLatch.await();
        return "关门";
    }


    @GetMapping("by")
    @ResponseBody
    public String by(){
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("countDownLatch");

        countDownLatch.countDown();
        return "离开"+Thread.currentThread().getName();
    }



}
