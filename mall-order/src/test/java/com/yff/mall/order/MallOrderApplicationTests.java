package com.yff.mall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class MallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        /*ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.lock();
        try {
            this.wait();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }*/


        //amqpAdmin.declareExchange(new DirectExchange("direct", true, false));
    }

    @Test
    void Queue() {
       /* amqpAdmin.declareQueue(new Queue("directQueue", true, false, false));
        Binding binding = new Binding("directQueue",
                Binding.DestinationType.QUEUE,
                "direct", "hello", null);
        amqpAdmin.declareBinding(binding);*/
    }

    @Test
    void Bing() {
/*
        Binding binding = new Binding("directQueue",
                Binding.DestinationType.QUEUE,
                "direct", "hello", null);
        amqpAdmin.declareBinding(binding);*/
    }

    @Test
    void send() {
        /*for (int i = 0; i < 10; i++) {
            if(i % 2 == 0){
                rabbitTemplate.convertAndSend("direct",
                        "hello",
                        "helloWorld"+i);
            }else{
                rabbitTemplate.convertAndSend("direct",
                        "hello",
                        i*10);
            }
        }*/
    }



    static int count = 0;
    public static void main() throws InterruptedException {
        Thread t1 = new Thread(()->{
            for (int i = 1;i<100;i++){
                count++;
            }
        });
        Thread t2 =new Thread(()->{
            for (int i = 1;i<100;i++){
                count--;
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("count的值是{}",count);
    }

    volatile static boolean flag = true;
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while (flag){}
        }).start();
        TimeUnit.SECONDS.sleep(1);
        flag = false;
    }

}
