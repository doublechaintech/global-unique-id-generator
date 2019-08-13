package com.doublechain.idgen.snowflake;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "doublechain.idgen.snowflake")
public class SnowflakeZookeeperConfigure {

    private ZookeeperConfigure zookeeper;

    private String group;

    private int dataCenter;

    private String startTimestamp;

    @Data
    public static class ZookeeperConfigure {

        private String servers;

        private int sessionTimeoutMs;

        private String namespace;
    }
}