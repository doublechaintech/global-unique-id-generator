package com.doublechain.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gaopeng on 2018/3/30.
 */
public class IdSpace {
    private int replenishThreshold;
    private String spaceName;
    private AtomicLong from;
    private long to;
    private int seqLength;
    private String prefix;
    private String suffix;

    public boolean isEmpty() {
        return from.get() == to;
    }

    public boolean needReplenish() {
        return to - from.get() <= replenishThreshold;
    }

    public int getReplenishThreshold() {
        return replenishThreshold;
    }

    public void setReplenishThreshold(int replenishThreshold) {
        this.replenishThreshold = replenishThreshold;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }


    public AtomicLong getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = new AtomicLong(from);
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getSeqLength() {
        return seqLength;
    }

    public void setSeqLength(int seqLength) {
        this.seqLength = seqLength;
    }

    @Override
    public String toString() {
        return "IdSpace{" +
                "replenishThreshold=" + replenishThreshold +
                ", spaceName='" + spaceName + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", seqLength=" + seqLength +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                '}';
    }
}
