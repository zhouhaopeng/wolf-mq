package com.coke.mq.broker;

import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.store.CommitLogItem;
import com.coke.wolf.mq.broker.store.DefaultMessageStore;
import java.io.File;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 5:55 下午
 */
public class DefaultMessageTest2 {

    private String topic = "Test_Topci_One";

    private String body = "Hello mq";

    private BrokerConfig brokerConfig = new BrokerConfig();

    @Before
    public void before() {
        File file = new File(BrokerConfig.ROOT_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void putAndGetMsg() {

        DefaultMessageStore defaultMessageStore = new DefaultMessageStore(brokerConfig);

        defaultMessageStore.createTopic(topic, 10);

        String[] bodys = {"java", "c", "php", "python"};

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                CommitLogItem commitLogItem = new CommitLogItem();

                commitLogItem.setTopic(topic);
                commitLogItem.setQueueId(i);
                String body = bodys[j % 4];
                byte[] bodyBuf = body.getBytes(Charset.forName("UTF-8"));
                commitLogItem.setBody(bodyBuf);
                defaultMessageStore.putMessage(commitLogItem);
            }
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("queueId==>" + i);
            for (int j = 0; j < 10; j++) {
                CommitLogItem commitLogItem = defaultMessageStore.getMessage(topic, i, j * BrokerConfig.INDEX_FILE_UNIT);
                System.out.println(commitLogItem + " ,body=" + new String(commitLogItem.getBody(), Charset.forName("UTF-8")));
            }
        }
    }

    @Test
    public void getMsg() {
        DefaultMessageStore defaultMessageStore = new DefaultMessageStore(brokerConfig);

        for (int i = 0; i < 10; i++) {
            System.out.println("queueId==>" + i);
            CommitLogItem commitLogItem = defaultMessageStore.getMessage(topic, i, 0);
            System.out.println(commitLogItem + " ,body=" + new String(commitLogItem.getBody(), Charset.forName("UTF-8")));
        }
    }

    @Test
    public void getMsg2() {
        DefaultMessageStore defaultMessageStore = new DefaultMessageStore(brokerConfig);

        for (int i = 0; i < 10; i++) {
            System.out.println("queueId==>" + i);
            for (int j = 0; j < 10; j++) {
                CommitLogItem commitLogItem = defaultMessageStore.getMessage(topic, i);
                System.out.println(commitLogItem + " ,body=" + new String(commitLogItem.getBody(), Charset.forName("UTF-8")));
                if (i % 2 == 0) {
                    break;
                }
            }
        }
    }
}
