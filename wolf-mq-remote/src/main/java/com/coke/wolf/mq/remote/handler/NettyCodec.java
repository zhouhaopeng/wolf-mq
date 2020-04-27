package com.coke.wolf.mq.remote.handler;

import com.coke.wolf.mq.remote.RemoteCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 10:13 下午
 */
public class NettyCodec extends ByteToMessageCodec<RemoteCommand> {

    @Override protected void encode(ChannelHandlerContext ctx, RemoteCommand msg, ByteBuf out) throws Exception {

        byte[] content = RemoteCommand.encode(msg);
        out.writeBytes(Unpooled.copiedBuffer(content));
    }

    @Override protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        RemoteCommand command = RemoteCommand.decode(in);
        out.add(command);
    }
}
