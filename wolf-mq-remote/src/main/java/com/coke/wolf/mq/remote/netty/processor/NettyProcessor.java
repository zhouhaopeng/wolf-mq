package com.coke.wolf.mq.remote.netty.processor;

import com.coke.wolf.mq.remote.RemoteCommand;
import io.netty.channel.ChannelHandlerContext;
import jdk.nashorn.internal.objects.annotations.Function;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 11:47 上午
 */
@FunctionalInterface
public interface NettyProcessor {

    void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand);

}
