package com.coke.wolf.mq.client;

import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.mq.client.model.Message;
import com.coke.wolf.mq.client.model.SendResult;
import com.coke.wolf.mq.client.producer.core.SyncProducer;
import java.nio.charset.Charset;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/28 11:00 下午
 */
public class ClientTest {

    private static String topic = "Test_Topci_One";

    public static void main(String[] args) {

        SyncProducer syncProducer = new SyncProducer();

        syncProducer.setNameSrv("127.0.0.1:9999");
        syncProducer.subscribe(topic);

        syncProducer.start();

        String[] bodys = {"java", "c", "php", "python"};

        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            Message message = new Message();
            message.setTopic(topic);
            String body = bodys[i % 4];
            byte[] bodyBuf = body.getBytes(Charset.forName("UTF-8"));
            message.setBody(bodyBuf);
            SendResult result = syncProducer.send(message);
            System.out.println("index=" + i + ",result = " + GsonUtil.gsonString(result));
        }
        long endTime = System.currentTimeMillis();

        System.out.println(endTime - beginTime);
        System.out.println((endTime - beginTime) / 1000);
    }
}
