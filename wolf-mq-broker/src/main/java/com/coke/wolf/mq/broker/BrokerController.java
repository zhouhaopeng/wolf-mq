package com.coke.wolf.mq.broker;

import com.coke.wolf.mq.broker.registry.NameSrvRegistry;
import com.coke.wolf.mq.broker.store.DefaultMessageStore;
import com.coke.wolf.common.model.store.NameSrvData;
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
 * @date 2020/4/22 11:52 上午
 */
public class BrokerController extends BrokerConfig {

    private final String name;

    private final NameSrvData nameSrvData;

    private DefaultMessageStore defaultMessageStore;

    private NameSrvRegistry nameSrvRegistry;

    private DefaultRequestProcessor defaultRequestProcessor;

    private BrokerProcessorRegistry brokerProcessorRegistry;

    private RemoteServer remoteServer;

    private ChannelHandler brokerServerHandler;

    public BrokerController(String name, String nameSrvAddr, int nameSrvPort) {

        this.name = name;
        nameSrvData = new NameSrvData(nameSrvAddr, nameSrvPort);

        defaultMessageStore = new DefaultMessageStore(this);

        nameSrvRegistry = new NameSrvRegistry(this);

        defaultRequestProcessor = new DefaultRequestProcessor();
        brokerProcessorRegistry = new BrokerProcessorRegistry(this);

        brokerProcessorRegistry.register();

        prepareHandler();
        remoteServer = new NettyRemoteServer(brokerServerHandler);
    }

    private void prepareHandler() {
        brokerServerHandler = new BrokerServerHandler();
    }

    public void start() {
        nameSrvRegistry.start();

        remoteServer.bind(BROKER_PORT);
    }

    public NameSrvData getNameSrvData() {
        return nameSrvData;
    }

    public DefaultMessageStore getDefaultMessageStore() {
        return defaultMessageStore;
    }

    public String getName() {
        return name;
    }

    public DefaultRequestProcessor getDefaultRequestProcessor() {
        return defaultRequestProcessor;
    }

    @ChannelHandler.Sharable
    class BrokerServerHandler extends SimpleChannelInboundHandler<RemoteCommand> {

        @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
            defaultRequestProcessor.processRemoteCommandRequest(ctx, msg);
        }

        @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

}
