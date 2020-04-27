package com.coke.wolf.mq.remote.netty;

import com.coke.wolf.mq.remote.RemoteServer;
import com.coke.wolf.mq.remote.handler.NettyCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.NetworkServer;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 9:52 下午
 */
public class NettyRemoteServer implements RemoteServer {

    private static final Logger logger = LogManager.getLogger(NetworkServer.class);

    private ChannelHandler serverHandler;

    private ServerBootstrap serverBootstrap;

    private NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(50);

    private volatile boolean started = false;

    public NettyRemoteServer(ChannelHandler serverHandler) {
        this.serverHandler = serverHandler;
        init();
    }

    @Override public void bind(int port) {
        logger.info("netty server is starting bind, port " + port);

        if (!started) {
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port));
            future.syncUninterruptibly();
        }
        logger.info("netty server bind port " + port + " success ");
    }

    private void init() {
        serverBootstrap = new ServerBootstrap().group(boosGroup, workGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .option(ChannelOption.SO_REUSEADDR, true)
            .option(ChannelOption.SO_KEEPALIVE, false)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_SNDBUF, 65535)
            .childOption(ChannelOption.SO_RCVBUF, 65535)
            //.localAddress(new InetSocketAddress(port))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new LoggingHandler())
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new LengthFieldPrepender(4))
                        .addLast(new NettyCodec())
                        .addLast(serverHandler);

                }
            });
    }
}
