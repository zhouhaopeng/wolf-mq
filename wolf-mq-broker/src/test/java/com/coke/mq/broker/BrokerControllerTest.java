package com.coke.mq.broker;

import com.coke.wolf.common.constant.BrokerProcessType;
import com.coke.wolf.common.model.broker.BrokerPullMsgDTO;
import com.coke.wolf.common.model.client.ClientPullMsgRequest;
import com.coke.wolf.common.model.client.ClientSendMsgRequest;
import com.coke.wolf.common.model.RemoteResponse;
import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.common.utils.KryoUtil;
import com.coke.wolf.mq.broker.BrokerController;
import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.netty.NettyRemoteClient;
import com.coke.wolf.mq.remote.netty.processor.AsyncRequestProcessor;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 10:48 下午
 */
public class BrokerControllerTest {

    private String topic = "Test_Topci_One";

    private BrokerController brokerController = new BrokerController("broker-a", "127.0.0.1", 9999);

    @Test
    public void register() throws IOException {
        brokerController.getDefaultMessageStore().createTopic(topic, 10);
        brokerController.start();
        System.in.read();
    }

    @Test
    public void putMsg() throws InterruptedException, IOException {

        NettyRemoteClient nettyRemoteClient = new NettyRemoteClient(new SimpleChannelInboundHandler<RemoteCommand>() {
            @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {

                RemoteResponse response = KryoUtil.deserializer(msg.getBody(), RemoteResponse.class);

            }
        });

        Channel channel = nettyRemoteClient.connect(BrokerController.getLocalHost(), BrokerController.BROKER_PORT);

        Thread.sleep(1000);

        String[] bodys = {"java", "c", "php", "python"};

        for (int i = 0; i < 100; i++) {

            for (int j = 0; j < 10; j++) {
                ClientSendMsgRequest request = new ClientSendMsgRequest();

                request.setTopic(topic);
                request.setQueueId(i);
                String body = bodys[j % 4];
                byte[] bodyBuf = body.getBytes(Charset.forName("UTF-8"));
                request.setBody(bodyBuf);

                RemoteCommand remoteCommand = RemoteCommand.build(BrokerProcessType.Client_Send_Msg_Req, KryoUtil.serializer(request));

                channel.writeAndFlush(remoteCommand);
            }
        }

        System.in.read();
    }

    @Test
    public void getMsg() throws InterruptedException {

        AsyncRequestProcessor asyncRequestProcessor = new AsyncRequestProcessor(20);

        NettyRemoteClient nettyRemoteClient = new NettyRemoteClient(new SimpleChannelInboundHandler<RemoteCommand>() {
            @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {
                asyncRequestProcessor.processRemoteCommandRequest(ctx, msg);

            }
        });

        Channel channel = nettyRemoteClient.connect(BrokerController.getLocalHost(), BrokerController.BROKER_PORT);
        Thread.sleep(1000);

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                ClientPullMsgRequest request = new ClientPullMsgRequest();

                request.setTopic(topic);
                request.setQueueId(i);
                request.setMaxCount(10);
                RemoteCommand sendCommand = RemoteCommand.build(BrokerProcessType.Client_pull_Msg_Req, KryoUtil.serializer(request));
                int requestId = sendCommand.getRequestId();
                asyncRequestProcessor.addQueueMap(requestId);
                channel.writeAndFlush(sendCommand);

                RemoteCommand remoteCommand = asyncRequestProcessor.getResult(requestId);

                RemoteResponse<List<BrokerPullMsgDTO>> response = KryoUtil.deserializer(remoteCommand.getBody(), RemoteResponse.class);

                System.out.println("pull msg " + GsonUtil.gsonString(response));
            }
        }

    }
}
