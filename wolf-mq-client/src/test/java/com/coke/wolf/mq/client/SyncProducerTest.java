package com.coke.wolf.mq.client;

import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.mq.client.manger.RouteInfoManager;
import com.coke.wolf.mq.client.model.Message;
import com.coke.wolf.mq.client.model.SendResult;
import com.coke.wolf.mq.client.producer.core.SyncProducer;
import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/26 9:37 下午
 */
public class SyncProducerTest {

    private static final Logger logger = LogManager.getLogger(SyncProducerTest.class);

    private String topic = "Test_Topci_One";

    @Test
    public void sendMsg() throws InterruptedException {

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
            logger.info("index=" + i + ",result = " + GsonUtil.gsonString(result));
        }
        long endTime = System.currentTimeMillis();

        System.out.println(endTime - beginTime);
        System.out.println((endTime - beginTime) / 1000);
    }

    @Test
    public void sendMsg2() throws InterruptedException {

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
            long begin = System.currentTimeMillis();
            SendResult result = syncProducer.send(message);
            long end = System.currentTimeMillis();
            long consumeTime = end - begin;
            System.out.println("index=" + i + ",result = " + GsonUtil.gsonString(result));
            System.out.println("consume time= " + consumeTime + ",second = " + consumeTime / 1000);
        }
        long endTime = System.currentTimeMillis();

        System.out.println(endTime - beginTime);
        System.out.println((endTime - beginTime) / 1000);
    }
}
