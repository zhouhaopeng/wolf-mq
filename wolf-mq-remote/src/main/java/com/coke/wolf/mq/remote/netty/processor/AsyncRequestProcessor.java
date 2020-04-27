package com.coke.wolf.mq.remote.netty.processor;

import com.coke.wolf.mq.remote.RemoteCommand;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 8:26 下午
 */
public class AsyncRequestProcessor implements NettyProcessor {

    private int timeOut;

    public AsyncRequestProcessor(int timeOut) {
        this.timeOut = timeOut;
    }

    private Map<Integer, SynchronousQueue<RemoteCommand>> synchronousQueueMap = Maps.newHashMap();

    public Map<Integer, SynchronousQueue<RemoteCommand>> getSynchronousQueueMap() {
        return synchronousQueueMap;
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {

        getSynchronousQueueMap().get(remoteCommand.getRequestId()).offer(RemoteCommand.copy(remoteCommand));
    }

    public void addQueueMap(int requestId) {
        synchronousQueueMap.putIfAbsent(requestId, new SynchronousQueue<>());
    }

    public RemoteCommand getResult(int requestId) {

        RemoteCommand remoteCommand = null;
        try {
            remoteCommand = synchronousQueueMap.get(requestId).poll(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return remoteCommand;
    }
}
