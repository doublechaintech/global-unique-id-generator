package com.doublechain.idgen.snowflake;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "doublechain.idgen.snowflake")
public class SnowflakeIgniteConfigure {

    private ZookeeperConfigure zookeeper;

    private String group;

    private int dataCenter;

    private String startTimestamp;
}
