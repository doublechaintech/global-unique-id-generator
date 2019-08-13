package com.doublechain.idgen.snowflake;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNodeIdentityProvider implements NodeIdentityProvider {

    private final NodeIdentity identity;

    public DefaultNodeIdentityProvider(long workerId, long dataCenterId, String group) {
        this.identity = new NodeIdentity(workerId, dataCenterId, group);
    }

    @Override
    public NodeIdentity provide() throws Exception {
        log.info("The specified node identity is {}", this.identity);
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
