package com.doublechain.id.starter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gaopeng on 2018/3/30.
 */
@Configuration
@EnableConfigurationProperties(ServerConfig.class)
@ComponentScan(basePackages = {"com.doublechain.id.starter"})
public class ServerAutoConfig {

}
