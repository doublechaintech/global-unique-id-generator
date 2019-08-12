package com.doublechain.idgen.snowflake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SnowflakeZookeeperConfigure.class)
@ConditionalOnClass({ZookeeperNodeIdentityProvider.class, SnowFlakeIdGenerator.class})
public class SnowflakeZookeeperAutoConfiguration {

    @Autowired
    private SnowflakeZookeeperConfigure configure;

    @Bean
    @ConditionalOnMissingBean
    public NodeIdentityProvider nodeIdentityProvider() {
        ZookeeperConfigure zookeeper = configure.getZookeeper();
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
        return new SnowFlakeIdGenerator(nodeIdentityProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeAppRunner runner() {
        return new SnowflakeAppRunner();
    }
}
