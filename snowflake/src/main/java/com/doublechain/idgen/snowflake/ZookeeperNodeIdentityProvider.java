package com.doublechain.idgen.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

@Slf4j
public class ZookeeperNodeIdentityProvider implements NodeIdentityProvider {

    private static final String DEFAULT_NAMESPACE = "snowflake";

    private static final int DEFAULT_SESSION_TIMEOUT_MS = 30000;

    private String group;

    private long dataCenter;

    private CuratorFramework client;

    public ZookeeperNodeIdentityProvider(String zkServers, int sessionTimeoutMs, String namespace, String group, long dataCenter) {
        this.group = group;
        this.dataCenter = dataCenter;
        this.client = CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(new RetryForever(3000))
                .namespace(namespace)
                .build();
    }

    public ZookeeperNodeIdentityProvider(String zkServers, String group, long dataCenter) {
        this(zkServers, DEFAULT_SESSION_TIMEOUT_MS, DEFAULT_NAMESPACE, group, dataCenter);
    }

    public ZookeeperNodeIdentityProvider(String zkServers, int sessionTimeoutMs, String group, long dataCenter) {
        this(zkServers, sessionTimeoutMs, DEFAULT_NAMESPACE, group, dataCenter);
    }

    public ZookeeperNodeIdentityProvider(String zkServers, String namespace, String group, long dataCenter) {
        this(zkServers, DEFAULT_SESSION_TIMEOUT_MS, namespace, group, dataCenter);
    }

    @Override
    public void start() throws Exception {
        if (client.getState() == CuratorFrameworkState.LATENT) {
            client.start();
        }
        String dataCenterSpace = String.format("/%s/dc_%d", group, dataCenter);
        Stat stat = client.checkExists().creatingParentContainersIfNeeded().forPath(dataCenterSpace);
        if (stat == null) {
            try {
                client.create().forPath(dataCenterSpace);
            } catch (KeeperException.NodeExistsException e) {
                // keep empty
            }
        }
    }

    @Override
    public void stop() {
        if (client.getState() != CuratorFrameworkState.STOPPED) {
            client.close();
        }
    }

    @Override
    public NodeIdentity provide() throws Exception {
        for (int i = 1; i <= SnowFlakeIdGenerator.maxWorkerId; i++) {
            String workerPath = String.format("/%s/dc_%d/worker_%d", group, dataCenter, i);
            try {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(workerPath);
                String groupSpace = String.format("/%s", group);
                Stat groupStat = new Stat();
                client.getData().storingStatIn(groupStat).forPath(groupSpace);
                long ctime = groupStat.getCtime();
                NodeIdentity nodeIdentity = new NodeIdentity(i, dataCenter, group, ctime);
                log.info("Get node identity from zookeeper, the identity is {}", nodeIdentity);
                return nodeIdentity;
            } catch (KeeperException.NodeExistsException e) {
                // keep empty
            }
        }
        throw new IllegalStateException(String.format("Snowflake only support %d workers in a data center", SnowFlakeIdGenerator.maxWorkerId));
    }
}
