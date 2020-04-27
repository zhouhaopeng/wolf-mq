package com.coke.wolf.mq.namesrv.processor;

import com.coke.wolf.common.constant.NameSrvProcessType;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.model.client.ClientQueryTopicRequest;
import com.coke.wolf.common.model.namesrv.NameSrvQueryTopicDTO;
import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.namesrv.NameSrvController;
import com.coke.wolf.common.model.store.QueueData;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 2:42 下午
 */
public class ClientRequestProcessor extends DefaultRequestProcessor {

    private NameSrvController nameSrvController;

    public ClientRequestProcessor(NameSrvController nameSrvController) {
        this.nameSrvController = nameSrvController;

        addProcessor(NameSrvProcessType.Client_Query_Topic_Req, clientQueryTopicReqProcessor);
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {
        getProcessor(remoteCommand.getType()).processRemoteCommandRequest(ctx, remoteCommand);

    }

    private NettyProcessor clientQueryTopicReqProcessor = (context, remoteCommand) -> {

        ClientQueryTopicRequest request = KryoUtil.deserializer(remoteCommand.getBody(), ClientQueryTopicRequest.class);

        List<NameSrvQueryTopicDTO> nameSrvQueryTopicDTOS = queryTopic(request.getTopic());
        RemoteResponse response = RemoteResponse.buildSuccess(nameSrvQueryTopicDTOS);
        context.writeAndFlush(RemoteCommand.build(remoteCommand.getType(), KryoUtil.serializer(response)));
    };

    private List<NameSrvQueryTopicDTO> queryTopic(String topic) {
        List<NameSrvQueryTopicDTO> nameSrvQueryTopicDTOS = Lists.newArrayList();

        if (nameSrvController.getRouteInfoManager().containTopic(topic)) {
            Map<String, QueueData> queueDataMap = nameSrvController.getRouteInfoManager().queryTopicData(topic);
            queueDataMap.forEach((k, v) -> {
                BrokerData brokerData = nameSrvController.getRouteInfoManager().queryBrokerData(k);

                nameSrvQueryTopicDTOS.add(new NameSrvQueryTopicDTO(v.getBrokerName(), brokerData.getAddress(), brokerData.getPort(), v.getTopic(),
                    v.getReadQueueNums(), v.getWriteQueueNums(), v.getPerm()));
            });
        }
        return nameSrvQueryTopicDTOS;
    }
}
