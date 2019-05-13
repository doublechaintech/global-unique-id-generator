package com.doublechain.id;

import com.alibaba.fastjson.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaopeng on 2018/3/30.
 */
public class IdSpaceIdGenerator {

    private Map<String, IdSpaceQueue> idSpaceHolder = new ConcurrentHashMap<>();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private String idServerUrl;

    private Lock lock = new ReentrantLock();


    public void setIdServerUrl(String idServerUrl) {
        this.idServerUrl = idServerUrl;
    }

    private IdSpace fetchIdSpace(String spaceName) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(idServerUrl + "?spaceName=" + spaceName).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(2000);
            conn.setUseCaches(false);
            conn.connect();
            return JSONObject.parseObject(conn.getInputStream(), Charset.forName("UTF-8"), IdSpace.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void initHolder(String spaceName) {
        lock.lock();
        try {
            IdSpaceQueue idSpaces = idSpaceHolder.get(spaceName);
            if (idSpaces == null) {
                idSpaces = new IdSpaceQueue();
                idSpaceHolder.put(spaceName, idSpaces);
            }
            IdSpace idSpace = fetchIdSpace(spaceName);
            if (idSpace != null) {
                idSpaces.offer(idSpace);
            }
        } finally {
            lock.unlock();
        }
    }

    private void replenish(String spaceName) {
        executor.execute(() -> {
            System.out.println("扩充idspace");
            IdSpaceQueue idSpaces = idSpaceHolder.get(spaceName);
            idSpaces.lock.lock();
            try {
                IdSpace newIdSpace = fetchIdSpace(spaceName);
                if (newIdSpace != null) {
                    idSpaces.offer(newIdSpace);
                }
                idSpaces.replenishCondition.signalAll();
            } finally {
                idSpaces.lock.unlock();
            }
            System.out.println("扩充idspace完成");
        });
    }

    public String nextId(String spaceName) {
        if (!idSpaceHolder.containsKey(spaceName)) {
            initHolder(spaceName);
        }
        IdSpaceQueue idSpaces = idSpaceHolder.get(spaceName);
        if (idSpaces.isEmpty()) {
            idSpaces.lock.lock();
            try {
                idSpaces.replenishCondition.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(String.format("Can not find the specified id space with space name \"%s\"", spaceName));
            } finally {
                idSpaces.lock.unlock();
            }
        }
        IdSpace idSpace = idSpaces.peek();
        if (idSpace == null) {
            return null;
        }
        StringBuilder id = new StringBuilder();
        if (idSpace.getPrefix() != null && !idSpace.getPrefix().equals("")) {
            id.append(idSpace.getPrefix());
        }
        long seq = idSpace.getFrom().getAndIncrement();
        String seqStr = String.valueOf(seq);
        if (seqStr.length() < idSpace.getSeqLength()) {
            for (int i = 0; i < idSpace.getSeqLength() - seqStr.length(); i++) {
                id.append("0");
            }
        }
        id.append(seqStr);
        if (idSpace.getSuffix() != null && !idSpace.getSuffix().equals("")) {
            id.append(idSpace.getSuffix());
        }
        if (idSpaces.size() <= 1 && idSpace.needReplenish()) {
            replenish(spaceName);
        }
        if (idSpace.isEmpty()) {
            idSpaces.poll();
        }
        return id.toString();
    }

}
