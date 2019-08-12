package com.doublechain.idgen.snowflake;

import org.junit.Test;

/**
 * Created by gaopeng on 2018/4/3.
 */
public class SnowFlakeIdGeneratorTest {

    @Test
    public void testNextId() throws Exception {
        Thread thread1 = new Thread(new Task());
        thread1.start();
        Thread thread2 = new Thread(new Task());
        thread2.start();
        thread1.join();
        thread2.join();
    }

    class Task implements Runnable {

        @Override
        public void run() {
            NodeIdentityProvider provider = ZookeeperNodeIdentityProvider.builder(1, "test", "10.0.0.32:2181")
                    .namespace("my-snowflake").build();
            SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(provider);
            try {
                idGenerator.start();
                for (int i = 0; i < 10; i++) {
                    long id = idGenerator.nextId();
                    System.out.println(Thread.currentThread().getName() + "====" + id);
                }
                idGenerator.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
