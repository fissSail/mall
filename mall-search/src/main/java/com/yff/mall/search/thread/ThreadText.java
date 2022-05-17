package com.yff.mall.search.thread;

import java.util.concurrent.*;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.thread
 * @Description
 * @date 2022/1/23 10:40
 */

public class ThreadText {

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        /*Thread thread = new Thread01();

        thread.start();
        System.out.println("main");

        new Thread(()->{
            System.out.println("runable");
        }).start();

        FutureTask futureTask = new FutureTask(() -> {
            System.out.println("FutureTask");
            return 1;
        });
        new Thread(futureTask).start();

        System.out.println(futureTask.get());

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.execute(()->{
            System.out.println("你好");
        });*/

        /**
         * int corePoolSize,核心线程数，线程池创建以后准备就绪的线程数量，
         * 一直存在，除非设置allowCoreThreadTimeOut
         * int maximumPoolSize,最大线程数
         * long keepAliveTime,存活时间，如果当前线程数大于corePoolSize且小于maximumPoolSize
         * 释放空闲的线程（maximumPoolSize-corePoolSize），只要线程空闲时间大于此时间
         * TimeUnit unit,时间单位
         * BlockingQueue<Runnable> workQueue,阻塞队列，如果任务很多，
         * 就把目前多的任务放入队列，只要有空闲线程，就去队列中拿任务执行
         * ThreadFactory threadFactory,线程的创建工厂
         * RejectedExecutionHandler handler，拒绝策略，队列满了，执行拒绝策略
         *
         * 工作顺序
         *  1，线程池创建，准备好一定数量的核心线程，等待接收任务
         *  2，核心线程数满了，就将多余的任务放入阻塞队列中，如果有空闲的核心线程就会自己去阻塞队列中获取任务执行
         *  3，阻塞队列满了，就再开新的线程执行任务，最大不超过maximumPoolSize最大线程数
         *  4，maximumPoolSize满了就用拒绝策略来自定义拒绝任务
         *  5，maximumPoolSize都执行完成，有空闲的线程，会在指定的时间keepAliveTime以后，释放maximumPoolSize-corePoolSize的线程
         */

        //ExecutorService executorService = Executors.newFixedThreadPool(10);

        //ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();

        //Executors.newCachedThreadPool();//核心线程为0，所有都可回收
        //Executors.newFixedThreadPool(10);//定长线程池，核心线程数=最大线程数，都不可回收
        //Executors.newSingleThreadExecutor();//单线程线程池，只有1个核心线程=最大线程数，以此从阻塞队列中拿任务执行
        //Executors.newScheduledThreadPool(10);//定时任务的线程池
        /*System.out.println("start");
        CompletableFuture.runAsync(()->{
            System.out.println("runAsync");
        }, Executors.newFixedThreadPool(10));
        System.out.println("end");*/

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //方法完成后的感知
        /*CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
                    int i = 10 / 0;
                    return i;
        }, Executors.newFixedThreadPool(10))
                .whenComplete((result,exception) ->
                        //得到线程执行结果+异常信息
                    System.out.println("结果"+result+"异常："+exception)
                ).exceptionally(throwable->{
                    //得到异常信息+改变返回结果
                    System.out.println(throwable);
                    return 10;
                });


        System.out.println(integerCompletableFuture.get());*/


        //方法完成后的处理
        /*CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
            int i = 10 / 0;
            return i;
        }, Executors.newFixedThreadPool(10))
                .handle((result,exception)->{
                    return result;
                });*/

        /*CompletableFuture<Void> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
            int i = 10 / 0;
            return i;
        }, Executors.newFixedThreadPool(10))
                .thenRunAsync(()->{
                    System.out.println("11");
                }, Executors.newFixedThreadPool(10));*/

        /*CompletableFuture<Void> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    System.out.println("supplyAsync");
                    int i = 10 / 5;
                    return i;
                }, Executors.newFixedThreadPool(10))
                .thenAcceptAsync(t->
                    System.out.println(t)
                , Executors.newFixedThreadPool(10));*/

        /*CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
                    System.out.println("supplyAsync");
                    int i = 10 / 5;
                    return i;
                }, Executors.newFixedThreadPool(10))
                .thenApplyAsync(t -> {
                    System.out.println("111");
                    return "aa==" + t;
                }, Executors.newFixedThreadPool(10));

        System.out.println(supplyAsync.get());*/

        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("1");
            int i = 10 / 3;
            return i;
        }, executorService);


        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "aaaa";
        }, executorService);

        /*future1.runAfterBothAsync(future2,()->{
            System.out.println(3);
        });*/
        /*future1.thenAcceptBothAsync(future2,(t,u)->{
            System.out.println(3);
            System.out.println(t+"----"+u);
        });*/

        /*CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (f1, f2) ->
                        "你好" + f1 + "---" + f2
                , executorService);

        System.out.println(future3.get());*/

        /*future1.runAfterEitherAsync(future2,()->{
            System.out.println("3");
        });*/

       /* future1.acceptEitherAsync(future2,res->{
            System.out.println(res);
        });*/

        /*CompletableFuture<String> stringCompletableFuture = future1.applyToEitherAsync(future2, t -> {
            return "你好"+t;
        });

        System.out.println(stringCompletableFuture.get());*/

    }


    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
            System.out.println("start");
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main开始");
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        //创建异步
        //有返回值
        /*CompletableFuture<Integer> integerCompletableFuture1 = CompletableFuture.supplyAsync(() -> 10 / 3, threadPool);
        System.out.println(integerCompletableFuture1.get());*/
        /*CompletableFuture<Integer> integerCompletableFuture2 = CompletableFuture.supplyAsync(() -> 999 / 5);
        System.out.println(integerCompletableFuture2.get());*/
        //无返回值
        /*CompletableFuture<Void> runnable = CompletableFuture.runAsync(() -> System.out.println("runnable"));
        System.out.println(runnable.get());*/

        /*CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 10 / 0, threadPool).whenCompleteAsync((result, throwable) -> {
            System.out.println(result);
            System.out.println(throwable);
        }, threadPool);
        System.out.println(completableFuture.get());*/

        /*CompletableFuture.supplyAsync(() -> 10 / 0, threadPool).whenComplete((result, throwable) -> {
            System.out.println(result);
            System.out.println(throwable);
        });*/

        /*CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> 10 / 0, threadPool).exceptionally(throwable -> {
            return 11;
        });
        System.out.println(exceptionally.get());*/


        /*CompletableFuture<Integer> exceptionally =
                CompletableFuture.supplyAsync(() -> 10 / 4, threadPool).handle((result, throwable)->{
                    System.out.println(result);
                    System.out.println(throwable);
                    return 10 * result;
                });
        System.out.println(exceptionally.get());*/

        /*CompletableFuture<Integer> exceptionally =
                CompletableFuture.supplyAsync(() -> 10 / 4, threadPool).handleAsync((result, throwable) -> {
                    System.out.println(result);
                    System.out.println(throwable);
                    return 10 * result;
                }, threadPool);
        System.out.println(exceptionally.get());*/

        CompletableFuture.supplyAsync(() -> 10 / 4, threadPool).thenAcceptAsync(res->{
            System.out.println("thenAcceptAsync");
        },threadPool);

        /*CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 10 / 4, threadPool).thenApplyAsync(res -> {
            System.out.println("thenApplyAsync");

            return 30 * res;
        }, threadPool);
        System.out.println(completableFuture.get());

        CompletableFuture.supplyAsync(() -> 10 / 4, threadPool).thenRunAsync(()->{
            System.out.println("thenRunAsync");
        },threadPool);*/


        System.out.println("main结束");
    }

}
