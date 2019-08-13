package com.doublechain.idgen.snowflake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableConfigurationProperties(SnowflakeZookeeperConfigure.class)
@ConditionalOnClass({ZookeeperNodeIdentityProvider.class, SnowFlakeIdGenerator.class})
public class SnowflakeZookeeperAutoConfiguration {

    @Autowired
    private SnowflakeZookeeperConfigure configure;

    @Bean
    @ConditionalOnMissingBean
    public NodeIdentityProvider nodeIdentityProvider() {
        SnowflakeZookeeperConfigure.ZookeeperConfigure zookeeper = configure.getZookeeper();
        String servers = zookeeper.getServers();
        int sessionTimeoutMs = zookeeper.getSessionTimeoutMs();
        String namespace = zookeeper.getNamespace();
        int dataCenter = configure.getDataCenter();
        String group = configure.getGroup();
        return ZookeeperNodeIdentityProvider.builder(dataCenter, group, servers)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SnowFlakeIdGenerator snowFlakeIdGenerator(NodeIdentityProvider nodeIdentityProvider) {
        final String startTimestamp = configure.getStartTimestamp();
        if (StringUtils.isEmpty(startTimestamp)) {
            return new SnowFlakeIdGenerator(nodeIdentityProvider);
        }
        long startEpochMs = LocalDateTime.parse(startTimestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new SnowFlakeIdGenerator(nodeIdentityProvider, startEpochMs);
    }

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeAppRunner runner() {
        return new SnowflakeAppRunner();
    }

}
