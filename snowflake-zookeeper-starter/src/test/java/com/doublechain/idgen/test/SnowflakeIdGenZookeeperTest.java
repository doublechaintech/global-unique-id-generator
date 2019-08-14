package com.doublechain.idgen.test;

import com.doublechain.idgen.snowflake.SnowFlakeIdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ZookeeperApplication.class})
public class SnowflakeIdGenZookeeperTest {

    @Autowired
    private SnowFlakeIdGenerator idGenerator;

    @Test
    public void testGenId() {
        for (int i = 0; i < 10; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}
