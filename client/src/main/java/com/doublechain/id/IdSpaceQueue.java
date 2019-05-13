package com.doublechain.id;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaopeng on 2018/4/2.
 */
public class IdSpaceQueue extends LinkedBlockingQueue<IdSpace> {

    Lock lock = new ReentrantLock(true);

    Condition replenishCondition = lock.newCondition();
}
