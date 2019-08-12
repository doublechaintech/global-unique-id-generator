package com.doublechain.idgen.snowflake;

import lombok.Data;

@Data
public class ZookeeperConfigure {

    private String servers;

    private int sessionTimeoutMs;

    private String namespace;
}
