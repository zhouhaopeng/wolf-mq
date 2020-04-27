package com.coke.wolf.mq.client.manger;

import com.coke.wolf.common.constant.NameSrvProcessType;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.model.client.ClientQueryTopicRequest;
import com.coke.wolf.common.model.namesrv.NameSrvQueryTopicDTO;
import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.model.store.NameSrvData;
import com.coke.wolf.common.model.store.QueueData;
import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.client.exception.WolfMqClientException;
import com.coke.wolf.mq.client.model.TopicData;
import com.coke.wolf.mq.remote.RemoteClient;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.NettyRemoteClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 9:51 下午
 */
public class RouteInfoManager {

    private static final Logger logger = LogManager.getLogger(RouteInfoManager.class);

    private NameSrvData nameSrvData;

    private RemoteClient remoteClient;

    private ChannelHandler routerInfoHandler;

    private Channel nameSrvChannel;

    private Random random = new Random();

    private static final Map<String/*broker*/, BrokerData> brokerTable = Maps.newConcurrentMap();

    private static final Map<String/*topic*/, Map<String, QueueData>> topicTable = Maps.newConcurrentMap();

    private Set<String> topics = Sets.newHashSet();

    //private static final Map<String, Map<String, Channel>> channelTable = Maps.newConcurrentMap();

    private static final SynchronousQueue<RemoteCommand> synchronousQueue = new SynchronousQueue();

    public RouteInfoManager() {
        prepareHandler();

        remoteClient = new NettyRemoteClient(routerInfoHandler);
    }

    public void subscribe(String nameSrv, String topic) {
        buildNameSrvData(nameSrv);
        topics.add(topic);
    }

    public void start() {
        connect();
        topics.stream().forEach(topic -> initRouterInfo(topic));
    }

    public List<TopicData> findTopicData(String topic) {
        Map<String, QueueData> queueDataMap = topicTable.get(topic);
        if (queueDataMap == null || queueDataMap.isEmpty()) {
            logger.warn("can't find " + topic + " 's broker");
            throw new WolfMqClientException("can't find " + topic + " 's broker");
        }
        List<TopicData> topicDataList = Lists.newArrayList();

        queueDataMap.values().stream().forEach(queueData -> {
            String brokerName = queueData.getBrokerName();
            BrokerData brokerData = brokerTable.get(brokerName);
            topicDataList.add(new TopicData(brokerName, brokerData, queueData));
        });

        return topicDataList;
    }

    private void initRouterInfo(String topic) {
        logger.info("client route manger begin fetch topic info");

        ClientQueryTopicRequest request = new ClientQueryTopicRequest();
        request.setTopic(topic);

        nameSrvChannel.writeAndFlush(RemoteCommand.build(NameSrvProcessType.Client_Query_Topic_Req, KryoUtil.serializer(request)));

        RemoteCommand remoteCommand = null;
        try {
            remoteCommand = synchronousQueue.poll(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new WolfMqClientException("subscribe NameServer time out");
        }

        RemoteResponse response = KryoUtil.deserializer(remoteCommand.getBody(), RemoteResponse.class);

        if (!response.isSuccess()) {
            logger.warn("topic " + topic + " not exist");
        }
        List<NameSrvQueryTopicDTO> nameSrvQueryTopicDTOS = (List<NameSrvQueryTopicDTO>) response.getData();

        logger.info("client received nameSrv topic info = " + GsonUtil.gsonString(nameSrvQueryTopicDTOS));
        buildRouterInfo(nameSrvQueryTopicDTOS);
    }

    private void buildRouterInfo(List<NameSrvQueryTopicDTO> nameSrvQueryTopicDTOS) {

        nameSrvQueryTopicDTOS.forEach(nameSrvQueryTopicDTO -> {

            brokerTable.putIfAbsent(nameSrvQueryTopicDTO.getBrokerName(), new BrokerData(nameSrvQueryTopicDTO.getBrokerName(),
                nameSrvQueryTopicDTO.getAddr(), nameSrvQueryTopicDTO.getPort()));

            Map<String, QueueData> queueDataMap = topicTable.get(nameSrvQueryTopicDTO.getTopic());
            if (queueDataMap == null) {
                queueDataMap = Maps.newConcurrentMap();
                topicTable.putIfAbsent(nameSrvQueryTopicDTO.getTopic(), queueDataMap);
            }
            queueDataMap.put(nameSrvQueryTopicDTO.getBrokerName(), new QueueData(nameSrvQueryTopicDTO.getBrokerName(),
                nameSrvQueryTopicDTO.getTopic(), nameSrvQueryTopicDTO.getReadQueueNums(), nameSrvQueryTopicDTO.getWriteQueueNums(), nameSrvQueryTopicDTO.getPerm()));
        });
    }

    private void connect() {
        nameSrvChannel = remoteClient.connect(nameSrvData.getAddr(), nameSrvData.getPort());
    }

    private void prepareHandler() {
        routerInfoHandler = new ClientRouterInfoHandler();
    }

    private void buildNameSrvData(String nameSrvAddr) {
        String[] strings = nameSrvAddr.split(":");
        String addr = strings[0];
        int port = Integer.parseInt(strings[1]);

        nameSrvData = new NameSrvData(addr, port);
    }

    @ChannelHandler.Sharable
    class ClientRouterInfoHandler extends SimpleChannelInboundHandler<RemoteCommand> {

        @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
            synchronousQueue.offer(msg);
        }
    }

}
