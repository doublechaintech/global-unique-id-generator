package com.doublechain.idgen.snowflake;

public interface NodeIdentityProvider {

    NodeIdentity provide() throws Exception;

    void start() throws Exception;

    boolean isStarted();

    void stop() throws Exception;
}
