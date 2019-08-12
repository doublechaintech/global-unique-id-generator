package com.doublechain.idgen.snowflake;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class SnowflakeAppRunner implements ApplicationRunner, DisposableBean {

    @Autowired
    private SnowFlakeIdGenerator idGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        idGenerator.start();
    }

    @Override
    public void destroy() throws Exception {
        idGenerator.stop();
    }
}
