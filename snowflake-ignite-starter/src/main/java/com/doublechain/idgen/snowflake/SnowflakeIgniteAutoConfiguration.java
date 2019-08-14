package com.doublechain.idgen.snowflake;

import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableConfigurationProperties(SnowflakeIgniteConfigure.class)
@ConditionalOnClass({IgniteNodeIdentityProvider.class, SnowFlakeIdGenerator.class})
public class SnowflakeIgniteAutoConfiguration {

    @Autowired
    private SnowflakeIgniteConfigure configure;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"doublechain.idgen.snowflake.ignite.multicast"})
    public NodeIdentityProvider nodeIdentityProviderUsingMulticast() {
        SnowflakeIgniteConfigure.IgniteConfigure ignite = configure.getIgnite();
        SnowflakeIgniteConfigure.IgniteConfigure.MulticastConfigure multicast = ignite.getMulticast();
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalPort(multicast.getLocalPort());
        discoverySpi.setLocalPortRange(multicast.getLocalPortRange());
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        if (!StringUtils.isEmpty(multicast.getGroupIp())) {
            ipFinder.setMulticastGroup(multicast.getGroupIp());
        }
        if (!StringUtils.isEmpty(multicast.getLocalAddress())) {
            ipFinder.setLocalAddress(multicast.getLocalAddress());
        }
        ipFinder.setMulticastPort(multicast.getMulticastPort());
        discoverySpi.setJoinTimeout(ignite.getJoinTimeout());
        if (!StringUtils.isEmpty(ignite.getLocalAddress())) {
            discoverySpi.setLocalAddress(ignite.getLocalAddress());
        }
        discoverySpi.setIpFinder(ipFinder);
        return new IgniteNodeIdentityProvider(configure.getGroup(), configure.getDataCenter(), discoverySpi);
    }

//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(value = {"doublechain.idgen.snowflake.ignite.zookeeper"})
//    public NodeIdentityProvider nodeIdentityProviderUsingZookeeper() {
//        SnowflakeIgniteConfigure.IgniteConfigure ignite = configure.getIgnite();
//        SnowflakeIgniteConfigure.IgniteConfigure.ZookeeperConfigure zookeeper = ignite.getZookeeper();
//        ZookeeperDiscoverySpi discoverySpi = new ZookeeperDiscoverySpi();
//        discoverySpi.setZkConnectionString(zookeeper.getServers());
//        discoverySpi.setJoinTimeout(ignite.getJoinTimeout());
//        discoverySpi.setZkRootPath(zookeeper.getRootPath());
//        discoverySpi.setSessionTimeout(zookeeper.getSessionTimeoutMs());
//        return new IgniteNodeIdentityProvider(configure.getGroup(), configure.getDataCenter(), discoverySpi);
//    }

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
    public SnowflakeIgniteRunner runner() {
        return new SnowflakeIgniteRunner();
    }

}
