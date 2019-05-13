package com.doublechain.id;

import org.junit.Test;

/**
 * Created by gaopeng on 2018/4/3.
 */
public class SnowFlakeIdGeneratorTest {

    @Test
    public void testNextId(){
        SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(1L,2L,0L);
        for (int i = 0; i < 100; i++) {
            long l = idGenerator.nextId();
            System.out.println(l);
        }
    }
}
