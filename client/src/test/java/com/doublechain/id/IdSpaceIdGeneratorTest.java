package com.doublechain.id;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by gaopeng on 2018/4/1.
 */
public class IdSpaceIdGeneratorTest {

    @Test
    public void testNextId() {
        IdSpaceIdGenerator idGenerator = new IdSpaceIdGenerator();
        idGenerator.setIdServerUrl("http://localhost:8080/id");
        CountDownLatch countDownLatch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String id = idGenerator.nextId("test");
                    System.out.println(Thread.currentThread().getName() + "===" + id);
                    Thread.yield();
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
