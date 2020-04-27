package com.coke.mq.broker;

import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.store.CommitLogItem;
import com.coke.wolf.mq.broker.store.ConsumeQueueItem;
import com.coke.wolf.mq.broker.store.DefaultMessageStore;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 5:55 下午
 */
public class DefaultMessageTest {

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

        String[] bodys = {"java", "c", "php", "python"};

        for (int i = 0; i < 10; i++) {

            CommitLogItem commitLogItem = new CommitLogItem();

            commitLogItem.setTopic(topic);
            commitLogItem.setQueueId(i);

            String body = bodys[i % 4];
            byte[] bodyBuf = body.getBytes(Charset.forName("UTF-8"));

            commitLogItem.setBody(bodyBuf);

            defaultMessageStore.putMessage(commitLogItem);

        }

        for (int i = 0; i < 10; i++) {
            System.out.println("queueId==>" + i);

            CommitLogItem commitLogItem = defaultMessageStore.getMessage(topic, i, 0);
            System.out.println(commitLogItem + " ,body=" + new String(commitLogItem.getBody(), Charset.forName("UTF-8")));
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
            List<CommitLogItem> commitLogItems = defaultMessageStore.getMessages(topic, i,10);
            System.out.println("i= "+i+",result = "+GsonUtil.gsonString(commitLogItems));
        }
    }
}
