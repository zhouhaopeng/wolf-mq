package com.coke.wolf.mq.remote;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/29 12:18 上午
 */
public class ResponseFuture {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private RemoteCommand remoteCommand;

    public ResponseFuture() {
    }

    public void putResponse(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
        countDownLatch.countDown();
    }

    public RemoteCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.remoteCommand;
    }
}

