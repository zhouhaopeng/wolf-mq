package com.coke.wolf.mq.client.producer.core;

import com.coke.wolf.common.constant.BrokerProcessType;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.model.client.ClientSendMsgRequest;
import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.common.utils.NumberUtils;
import com.coke.wolf.mq.client.manger.RouteInfoManager;
import com.coke.wolf.mq.client.model.Message;
import com.coke.wolf.mq.client.model.SendResult;
import com.coke.wolf.mq.client.model.TopicData;
import com.coke.wolf.mq.client.producer.ClientConfig;
import com.coke.wolf.mq.client.producer.Producer;
import com.coke.wolf.mq.remote.RemoteClient;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.NettyRemoteClient;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;
import com.google.common.collect.Lists;
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
 * @date 2020/4/23 10:49 上午
 */
public abstract class AbstractProducer extends ClientConfig implements Producer {

    protected static final Logger logger = LogManager.getLogger(Producer.class);

    private String nameSrvAddr;

    private RemoteClient remoteClient;

    private ChannelHandler clientHandler;

    private RouteInfoManager routeInfoManager;

    private Map<String/*topic*/, List<TopicData>> topicDataMap = Maps.newConcurrentMap();

    private Map<BrokerData, List<Channel>> brokerChannelMap = Maps.newConcurrentMap();

    public AbstractProducer() {

        prepareHandler();
        remoteClient = new NettyRemoteClient(clientHandler);
        routeInfoManager = new RouteInfoManager();
    }

    public void subscribe(String topic) {
        routeInfoManager.subscribe(this.nameSrvAddr, topic);
    }

    private void prepareHandler() {
        clientHandler = new ClientHandler();
    }

    @Override public void setNameSrv(String addr) {
        nameSrvAddr = addr;
    }

    @Override public void start() {
        routeInfoManager.start();
    }

    protected TopicData findTopicData(String topic) {
        List<TopicData> topicDataList = topicDataMap.computeIfAbsent(topic, t -> routeInfoManager.findTopicData(t));

        TopicData topicData = topicDataList.get(NumberUtils.random(topicDataList.size()));
        if (topicData.getChannels() == null || topicData.getChannels().isEmpty()) {
            initTopicData(topicData);
        }
        return topicData;
    }

    private void initTopicData(TopicData topicData) {
        BrokerData brokerData = topicData.getBrokerData();

        logger.info("init topic data broker " + brokerData.getName());
        synchronized (brokerData) {

            List<Channel> channelList = brokerChannelMap.get(brokerData);
            if (channelList == null || channelList.isEmpty()) {
                channelList = getConnect(brokerData);
                brokerChannelMap.putIfAbsent(brokerData, channelList);
            }
            topicData.setChannels(channelList);
        }
    }

    private List<Channel> getConnect(BrokerData brokerData) {
        List<Channel> channelList = Lists.newArrayList();
        for (int i = 0; i < 1; i++) {
            channelList.add(connect(brokerData));
        }

        return channelList;
    }

    private Channel connect(BrokerData brokerData) {
        return remoteClient.connect(brokerData.getAddress(), brokerData.getPort());
    }

    @ChannelHandler.Sharable
    class ClientHandler extends SimpleChannelInboundHandler<RemoteCommand> {

        @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
            getProcessor().processRemoteCommandRequest(ctx, msg);
        }

        @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

    protected ClientSendMsgRequest buildRequest(Message message) {
        ClientSendMsgRequest request = new ClientSendMsgRequest();

        request.setTopic(message.getTopic());
        request.setBody(message.getBody());

        return request;
    }

    protected SendResult convertResp(RemoteCommand remoteCommand) {
        SendResult result = null;
        if (remoteCommand != null) {
            RemoteResponse response = KryoUtil.deserializer(remoteCommand.getBody(), RemoteResponse.class);
            result = SendResult.build(response.isSuccess(), response.getCode(), response.getMsg());
        }
        return result;
    }

    abstract NettyProcessor getProcessor();

}
