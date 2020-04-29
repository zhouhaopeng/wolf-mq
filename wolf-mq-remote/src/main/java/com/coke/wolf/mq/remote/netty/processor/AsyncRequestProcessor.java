package com.coke.wolf.mq.remote.netty.processor;

import com.coke.wolf.mq.remote.ResponseFuture;
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

    private Map<Integer, ResponseFuture> responseFutureMap = Maps.newHashMap();

    public Map<Integer, ResponseFuture> getResponseFutureMap() {
        return responseFutureMap;
    }

    public void setResponseFutureMap(
        Map<Integer, ResponseFuture> responseFutureMap) {
        this.responseFutureMap = responseFutureMap;
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {

        ResponseFuture responseFuture = getResponseFutureMap().get(remoteCommand.getRequestId());
        if (responseFuture != null) {
            responseFuture.putResponse(remoteCommand);
        }
    }

    public ResponseFuture addQueueMap(int requestId) {
        ResponseFuture responseFuture = new ResponseFuture();
        responseFutureMap.putIfAbsent(requestId, responseFuture);
        return responseFuture;
    }

}
