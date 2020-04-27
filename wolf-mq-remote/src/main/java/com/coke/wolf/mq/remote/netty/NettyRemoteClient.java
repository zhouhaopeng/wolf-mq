package com.coke.wolf.mq.remote.netty;

import com.coke.wolf.mq.remote.RemoteClient;
import com.coke.wolf.mq.remote.handler.NettyCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 10:08 下午
 */
public class NettyRemoteClient implements RemoteClient {

    private static final Logger logger = LogManager.getLogger(NettyRemoteClient.class);

    private ChannelHandler clientHandler;

    private Bootstrap bootstrap;

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(50);

    public NettyRemoteClient(ChannelHandler clientHandler) {
        this.clientHandler = clientHandler;

        init();
    }

    @Override public Channel connect(String address, int port) {
        logger.info("netty client is starting connect address " + address + ":" + port);

        ChannelFuture future = null;
        try {
            future = bootstrap.connect(new InetSocketAddress(address, port));
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("netty client connected " + address + ":" + port + " success ");
        return future.channel();
    }

    private void init() {
        bootstrap = new Bootstrap().group(workGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_SNDBUF, 65535)
            .option(ChannelOption.SO_RCVBUF, 65535)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new LoggingHandler())
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new LengthFieldPrepender(4))
                        .addLast(new NettyCodec())
                        .addLast(clientHandler);
                }
            });
    }
}
