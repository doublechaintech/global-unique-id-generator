package com.doublechain.idgen.snowflake;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "doublechain.idgen.snowflake")
public class SnowflakeIgniteConfigure {

    private IgniteConfigure ignite;

    private String group;

    private int dataCenter;

    private String startTimestamp;

    @Data
    public static class IgniteConfigure {

        private MulticastConfigure multicast;

//        private ZookeeperConfigure zookeeper;

        private int joinTimeout;

        private String localAddress;

        @Data
        public static class MulticastConfigure {

            private String groupIp;

            private int localPort = 47500;

            private int localPortRange = 100;

            private String localAddress;

            private int multicastPort = 47400;

        }

//        @Data
//        public static class ZookeeperConfigure {
//
//            private String servers;
//
//            private int sessionTimeoutMs;
//
//            private String rootPath;
//        }
    }
}
