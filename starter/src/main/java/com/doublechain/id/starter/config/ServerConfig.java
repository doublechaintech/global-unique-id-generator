package com.doublechain.id.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by gaopeng on 2018/3/30.
 */
@ConfigurationProperties(prefix = "doublechain.id-generator.server")
@Data
public class ServerConfig {

    private String type;

}
