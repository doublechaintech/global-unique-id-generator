package com.doublechain.idgen.snowflake;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeIdentity {

    private long workerId;

    private long dataCenterId;

    private String group;

    private long startEpochMs;

}
