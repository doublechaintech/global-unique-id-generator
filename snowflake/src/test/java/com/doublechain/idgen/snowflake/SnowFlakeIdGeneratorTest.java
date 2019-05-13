package com.doublechain.idgen.snowflake;

import org.junit.Test;

/**
 * Created by gaopeng on 2018/4/3.
 */
public class SnowFlakeIdGeneratorTest {

    @Test
    public void testNextId() throws Exception {
        NodeIdentityProvider provider = new ZookeeperNodeIdentityProvider("xstardev.jios.org:20181,xstardev.jios.org:21181,xstardev.jios.org:22181", "test", 1);
        SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(provider);
        idGenerator.start();
        for (int i = 0; i < 100; i++) {
            long l = idGenerator.nextId();
            System.out.println(l);
        }
        idGenerator.stop();
    }
}
