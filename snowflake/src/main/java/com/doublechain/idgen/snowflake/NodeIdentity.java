package com.doublechain.idgen.snowflake;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class NodeIdentity {

    private long workerId;

    private long dataCenterId;

    private String group;

}
