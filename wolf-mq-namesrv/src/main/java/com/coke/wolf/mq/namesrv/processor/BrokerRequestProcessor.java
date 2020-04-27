package com.coke.wolf.mq.namesrv.processor;

import com.coke.wolf.common.constant.NameSrvProcessType;
import com.coke.wolf.common.model.broker.BrokerRegisterRequest;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.namesrv.NameSrvController;
import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.model.store.QueueData;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 5:11 下午
 */
public class BrokerRequestProcessor extends DefaultRequestProcessor {

    private NameSrvController nameSrvController;

    public BrokerRequestProcessor(NameSrvController nameSrvController) {
        this.nameSrvController = nameSrvController;

        addProcessor(NameSrvProcessType.Broker_Register_Req, brokerRegisterProcessor);
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {
        getProcessor(remoteCommand.getType()).processRemoteCommandRequest(ctx, remoteCommand);
    }

    private NettyProcessor brokerRegisterProcessor = (ChannelHandlerContext ctx, RemoteCommand remoteCommand) ->

    {
        BrokerRegisterRequest request = KryoUtil.deserializer(remoteCommand.getBody(), BrokerRegisterRequest.class);

        String brokerName = request.getBrokerName();

        BrokerData brokerData = convertToBroker(request);
        nameSrvController.getRouteInfoManager().saveBrokerData(brokerName, brokerData);

        Map<String, QueueData> queueDataMap = convertToQueueData(brokerName, request.getQueueDataRequestMap());
        if (queueDataMap != null) {
            queueDataMap.forEach((k, v) ->
                nameSrvController.getRouteInfoManager().saveTopicData(k, v)
            );
        }
        byte[] resp = KryoUtil.serializer(RemoteResponse.buildSuccess());
        ctx.writeAndFlush(RemoteCommand.build(remoteCommand.getType(), resp));
    };

    private BrokerData convertToBroker(BrokerRegisterRequest request) {
        BrokerData brokerData = new BrokerData();
        brokerData.setName(request.getBrokerName());
        brokerData.setAddress(request.getAddress());
        brokerData.setPort(request.getPort());
        return brokerData;
    }

    private Map<String, QueueData> convertToQueueData(String brokerName,
        Map<String, BrokerRegisterRequest.QueueDataRequest> remoteQueueRequestMap) {
        Map<String, QueueData> queueDataMap = null;
        if (remoteQueueRequestMap != null && !remoteQueueRequestMap.isEmpty()) {

            queueDataMap = Maps.newHashMap();
            for (Map.Entry<String, BrokerRegisterRequest.QueueDataRequest> entry : remoteQueueRequestMap.entrySet()) {

                String topic = entry.getKey();
                BrokerRegisterRequest.QueueDataRequest queueDataRequest = entry.getValue();

                QueueData queueData = new QueueData();
                queueData.setBrokerName(brokerName);
                queueData.setTopic(topic);
                queueData.setReadQueueNums(queueDataRequest.getReadQueueNums());
                queueData.setWriteQueueNums(queueDataRequest.getWriteQueueNums());
                queueData.setPerm(queueDataRequest.getPerm());

                queueDataMap.put(topic, queueData);
            }
        }
        return queueDataMap;

    }

}
