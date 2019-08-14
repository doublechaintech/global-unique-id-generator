package com.doublechain.idgen.snowflake;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;

public class IgniteNodeIdentityProvider implements NodeIdentityProvider {


    private volatile boolean started = false;

    private Ignite ignite;

    private final String group;

    private final long dataCenter;

    private final IgniteDiscoverySpi discoverySpi;


    public IgniteNodeIdentityProvider(String group, long dataCenter, IgniteDiscoverySpi discoverySpi) {
        this.group = group;
        this.dataCenter = dataCenter;
        this.discoverySpi = discoverySpi;
    }


    @Override
    public NodeIdentity provide() throws Exception {
        String name = "NODE_IDENTITY:" + group + ":" + dataCenter;
        ClusterNode localNode = ignite.cluster().localNode();
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache(name);
        for (int i = 0; true; i++) {
            if (cache.containsKey(i)) {
                continue;
            }
            cache.put(i, localNode.id().toString());
            return new NodeIdentity(i, dataCenter, group);
        }
    }

    @Override
    public void start() throws Exception {
        if (isStarted()) {
            return;
        }
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(discoverySpi);
        ignite = Ignition.start(cfg);
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void stop() throws Exception {
        ignite.close();
        started = false;
    }
}
