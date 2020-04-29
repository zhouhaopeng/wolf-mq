package com.coke.wolf.mq.broker.processor;

import com.coke.wolf.common.constant.BrokerProcessType;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.model.broker.BrokerPullMsgDTO;
import com.coke.wolf.common.model.client.ClientPullMsgRequest;
import com.coke.wolf.common.model.client.ClientSendMsgRequest;
import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.common.utils.ThreadFactoryImpl;
import com.coke.wolf.mq.broker.BrokerController;
import com.coke.wolf.mq.broker.store.CommitLogItem;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.coke.wolf.common.constant.BrokerProcessType.Client_Send_Msg_Req;
import static com.coke.wolf.common.constant.BrokerProcessType.Client_pull_Msg_Req;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 11:43 上午
 */
public class ClientRequestProcessor implements NettyProcessor {

    private static final Logger logger = LogManager.getLogger(ClientRequestProcessor.class);

    private BrokerController brokerController;

    private Executor sendMsgExecutor = new ThreadPoolExecutor(1, 16, 3600, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(10000), new ThreadFactoryImpl("send_msg_factory_"));

    private Executor pullMsgExecutor = new ThreadPoolExecutor(20, 60, 3600, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(3000));

    public ClientRequestProcessor(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {

        int type = remoteCommand.getType();
        switch (type) {
            case Client_Send_Msg_Req:
                sendMsgExecutor.execute(new ProcessTask(ctx, remoteCommand, Client_Send_Msg_Req));
                break;
            case Client_pull_Msg_Req:
                pullMsgExecutor.execute(new ProcessTask(ctx, remoteCommand, Client_pull_Msg_Req));
                break;
            default:
                break;
        }

    }

    private void processClientSendMsgReq(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {
        byte[] msg = remoteCommand.getBody();
        ClientSendMsgRequest request = KryoUtil.deserializer(msg, ClientSendMsgRequest.class);

        CommitLogItem commitLogItem = convert(request);

        logger.info("received message =" + GsonUtil.gsonString(commitLogItem));
        long begin = System.currentTimeMillis();
        brokerController.getDefaultMessageStore().putMessage(commitLogItem);
        long end = System.currentTimeMillis();
        long consumeTime = end - begin;
        System.out.println("consume time= " + consumeTime + ",second = " + consumeTime / 1000);

        RemoteResponse response = RemoteResponse.buildSuccess();
        ctx.writeAndFlush(RemoteCommand.build(remoteCommand.getRequestId(), remoteCommand.getType(), KryoUtil.serializer(response)));
    }

    private void processClientPullMsgRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {

        RemoteResponse<List<BrokerPullMsgDTO>> response = RemoteResponse.buildSuccess();

        try {
            byte[] msg = remoteCommand.getBody();
            ClientPullMsgRequest request = KryoUtil.deserializer(msg, ClientPullMsgRequest.class);

            List<CommitLogItem> commitLogItems = brokerController.getDefaultMessageStore().getMessages(request.getTopic(), request.getQueueId(), request.getMaxCount());
            final List<BrokerPullMsgDTO> brokerPullMsgDTOS = Lists.newArrayList();
            commitLogItems.forEach(commitLogItem -> brokerPullMsgDTOS.add(convert(commitLogItem)));
            response.setData(brokerPullMsgDTOS);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(e.getMessage());
        }

        ctx.writeAndFlush(RemoteCommand.build(remoteCommand.getRequestId(), remoteCommand.getType(), KryoUtil.serializer(response)));
    }

    private BrokerPullMsgDTO convert(CommitLogItem commitLogItem) {
        BrokerPullMsgDTO brokerPullMsgDTO = new BrokerPullMsgDTO(commitLogItem.getTopic(), commitLogItem.getQueueId(), commitLogItem.getBody());
        return brokerPullMsgDTO;
    }

    private CommitLogItem convert(ClientSendMsgRequest request) {
        CommitLogItem commitLogItem = new CommitLogItem(request.getTopic(), request.getQueueId(), request.getBody());
        return commitLogItem;
    }

    class ProcessTask implements Runnable {

        private ChannelHandlerContext context;

        private RemoteCommand remoteCommand;

        private int type;

        public ProcessTask(ChannelHandlerContext context, RemoteCommand remoteCommand, int type) {
            this.context = context;
            this.remoteCommand = remoteCommand;
            this.type = type;
        }

        @Override public void run() {
            switch (type) {
                case Client_Send_Msg_Req:
                    processClientSendMsgReq(context, remoteCommand);
                    break;
                case Client_pull_Msg_Req:
                    processClientPullMsgRequest(context, remoteCommand);
                    break;
                default:
                    break;
            }

        }
    }
}
