package com.doublechain.idgen.snowflake;

public class DefaultNodeIdentityProvider implements NodeIdentityProvider {

    private final NodeIdentity identity;

    public DefaultNodeIdentityProvider(long workerId, long dataCenterId, String group, long startEpochMs) {
        this.identity = new NodeIdentity(workerId, dataCenterId, group, startEpochMs);
    }

    @Override
    public NodeIdentity provide() throws Exception {
        return this.identity;
    }

    @Override
    public void start() throws Exception {
        // keep empty
    }

    @Override
    public void stop() throws Exception {
        // keep empty
    }

    @Override
    public boolean isStarted() {
        return true;
    }
}
