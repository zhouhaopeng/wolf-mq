package com.coke.wolf.mq.namesrv;

import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.RemoteServer;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.NettyRemoteServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 4:15 下午
 */
public class NameSrvController extends NameSrvConfig {

    private RouteInfoManager routeInfoManager;

    private DefaultRequestProcessor defaultRequestProcessor;

    private NameSrvProcessorRegistry nameSrvProcessorRegistry;

    private ChannelHandler serverHandler;

    private RemoteServer remoteServer;

    public NameSrvController() {

        routeInfoManager = new RouteInfoManager();

        defaultRequestProcessor = new DefaultRequestProcessor();

        nameSrvProcessorRegistry = new NameSrvProcessorRegistry(this);
        nameSrvProcessorRegistry.register();

        serverHandler = new NameSrvHandler();
        remoteServer = new NettyRemoteServer(serverHandler);

    }

    public void start() {
        remoteServer.bind(port);
    }

    public RouteInfoManager getRouteInfoManager() {
        return routeInfoManager;
    }

    public void setRouteInfoManager(RouteInfoManager routeInfoManager) {
        this.routeInfoManager = routeInfoManager;
    }

    public DefaultRequestProcessor getDefaultRequestProcessor() {
        return defaultRequestProcessor;
    }

    public void setDefaultRequestProcessor(DefaultRequestProcessor defaultRequestProcessor) {
        this.defaultRequestProcessor = defaultRequestProcessor;
    }

    @ChannelHandler.Sharable
    public class NameSrvHandler extends SimpleChannelInboundHandler<RemoteCommand> {

        @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
            defaultRequestProcessor.processRemoteCommandRequest(ctx, msg);
        }
    }
}
