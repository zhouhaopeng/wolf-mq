package com.coke.wolf.mq.client.producer.core;

import com.coke.wolf.common.constant.BrokerProcessType;
import com.coke.wolf.common.model.client.ClientSendMsgRequest;
import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.client.model.Message;
import com.coke.wolf.mq.client.model.SendResult;
import com.coke.wolf.mq.client.model.TopicData;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.processor.AsyncRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;
import io.netty.channel.Channel;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/24 12:47 下午
 */
public class SyncProducer extends AbstractProducer {

    private AsyncRequestProcessor asyncRequestProcessor;

    public SyncProducer() {
        asyncRequestProcessor = new AsyncRequestProcessor(Time_Out);
    }

    @Override NettyProcessor getProcessor() {
        return asyncRequestProcessor;
    }

    @Override public SendResult send(Message message) {

        TopicData topicData = findTopicData(message.getTopic());
        Channel channel = topicData.getChannel();

        ClientSendMsgRequest request = buildRequest(message);
        request.setQueueId(topicData.randomQueueId(message.getQueueId()));

        RemoteCommand remoteCommand = RemoteCommand.build(BrokerProcessType.Client_Send_Msg_Req, KryoUtil.serializer(request));
        int requestId = remoteCommand.getRequestId();
        asyncRequestProcessor.addQueueMap(requestId);

        channel.writeAndFlush(remoteCommand);

        logger.info("client send request " + GsonUtil.gsonString(request));
        RemoteCommand resp = asyncRequestProcessor.getResult(requestId);

        return convertResp(resp);
    }
}
