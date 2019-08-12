package com.doublechain.idgen.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

@Slf4j
public class ZookeeperNodeIdentityProvider implements NodeIdentityProvider {

    private static final String DEFAULT_NAMESPACE = "snowflake";

    private static final int DEFAULT_SESSION_TIMEOUT_MS = 30000;

    private final String group;

    private final long dataCenter;

    private final CuratorFramework client;

    private volatile boolean started = false;

    private ZookeeperNodeIdentityProvider(Builder builder) {
        this.dataCenter = builder.dataCenterId;
        this.group = builder.group;
        final String zkServers = builder.zkServers;
        final String namespace = builder.namespace == null ? DEFAULT_NAMESPACE : builder.namespace;
        final Integer sessionTimeoutMs = builder.sessionTimeoutMs == null ? DEFAULT_SESSION_TIMEOUT_MS : builder.sessionTimeoutMs;
        this.client = CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(new RetryForever(3000))
                .namespace(namespace)
                .build();
    }

    public static Builder builder(long dataCenterId, String group, String zkServers) {
        return new Builder(dataCenterId, group, zkServers);
    }

    @Override
    public void start() throws Exception {
        if (started) {
            return;
        }
        log.info("Begin to get node identity from zookeeper");
        if (client.getState() == CuratorFrameworkState.LATENT) {
            client.start();
        }
        final String dataCenterSpace = String.format("/%s/dc_%d", group, dataCenter);
        final Stat stat = client.checkExists().creatingParentContainersIfNeeded().forPath(dataCenterSpace);
        if (stat == null) {
            try {
                client.create().forPath(dataCenterSpace);
            } catch (KeeperException.NodeExistsException e) {
                // keep empty
            }
        }
        started = true;
    }

    @Override
    public void stop() {
        if (client.getState() != CuratorFrameworkState.STOPPED) {
            client.close();
        }
        started = false;
    }

    @Override
    public NodeIdentity provide() throws Exception {
        for (int i = 1; i <= SnowFlakeIdGenerator.maxWorkerId; i++) {
            String workerPath = String.format("/%s/dc_%d/worker_%d", group, dataCenter, i);
            try {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(workerPath);
                final String groupSpace = String.format("/%s", group);
                final Stat groupStat = new Stat();
                client.getData().storingStatIn(groupStat).forPath(groupSpace);
                final long ctime = groupStat.getCtime();
                final NodeIdentity nodeIdentity = new NodeIdentity(i, dataCenter, group, ctime);
                log.info("Got node identity from zookeeper, the identity is {}", nodeIdentity);
                return nodeIdentity;
            } catch (KeeperException.NodeExistsException e) {
                // keep empty
            }
        }
        throw new IllegalStateException(String.format("Snowflake only support %d workers in a data center", SnowFlakeIdGenerator.maxWorkerId));
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    public static class Builder {

        private long dataCenterId;

        private String group;

        private String zkServers;

        private String namespace;

        private Integer sessionTimeoutMs;

        private Builder(long dataCenterId, String group, String zkServers) {
            this.dataCenterId = dataCenterId;
            this.group = group;
            this.zkServers = zkServers;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder sessionTimeoutMs(int sessionTimeoutMs) {
            this.sessionTimeoutMs = sessionTimeoutMs;
            return this;
        }

        public ZookeeperNodeIdentityProvider build() {
            return new ZookeeperNodeIdentityProvider(this);
        }
    }
}
