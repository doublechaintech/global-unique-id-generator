package com.doublechain.id.starter.database;

import lombok.Data;

/**
 * Created by gaopeng on 2018/3/30.
 */
@Data
public class IdSpaceDO {
    private Integer id;
    private String spaceName;
    private Integer steps;
    private Long seed;
    private Integer seqLength;
    private String prefix;
    private String suffix;
    private Integer replenishThreshold;
}
