package com.coke.wolf.mq.broker.registry;

import com.coke.wolf.common.constant.NameSrvProcessType;
import com.coke.wolf.common.model.broker.BrokerRegisterRequest;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.broker.BrokerController;
import com.coke.wolf.mq.broker.store.TopicData;
import com.coke.wolf.mq.remote.RemoteClient;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.NettyRemoteClient;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 9:53 下午
 */
public class NameSrvRegistry {

    private static final Logger logger = LogManager.getLogger(NameSrvRegistry.class);

    private BrokerController brokerController;

    private RemoteClient remoteClient;

    private ChannelHandler registryHandler;

    private Channel channel;

    public NameSrvRegistry(BrokerController brokerController) {
        this.brokerController = brokerController;

        prepareHandler();

        remoteClient = new NettyRemoteClient(registryHandler);
    }

    private void connect() {
        channel = remoteClient.connect(brokerController.getNameSrvData().getAddr(), brokerController.getNameSrvData().getPort());
    }

    public void start() {

        connect();
        registry();
    }

    private void registry() {
        logger.info("nameSrv registry is starting registry ");

        RemoteCommand remoteCommand = buildRequest();
        channel.writeAndFlush(remoteCommand);
    }

    private RemoteCommand buildRequest() {

        BrokerRegisterRequest request = new BrokerRegisterRequest();
        request.setBrokerName(brokerController.getName());
        request.setAddress(brokerController.getLocalHost());
        request.setPort(brokerController.BROKER_PORT);

        List<TopicData> topicData = brokerController.getDefaultMessageStore().getTopic();
        if (!topicData.isEmpty()) {
            final Map<String, BrokerRegisterRequest.QueueDataRequest> remoteQueueRequestMap = Maps.newHashMap();
            topicData.forEach(t -> {
                remoteQueueRequestMap.putIfAbsent(t.getTopic(), new BrokerRegisterRequest.QueueDataRequest(t.getTopic(), t.getReadQueueNums(), t.getWriteQueueNums()));
            });
            request.setQueueDataRequestMap(remoteQueueRequestMap);
        }
        byte[] body = KryoUtil.serializer(request);
        return RemoteCommand.build(NameSrvProcessType.Broker_Register_Req, body);

    }

    private void prepareHandler() {
        registryHandler = new BrokerRegistryHandler();
    }

    class BrokerRegistryHandler extends SimpleChannelInboundHandler<RemoteCommand> {

        @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
            logger.info("nameSrv registry registered success ");
        }
    }

}
