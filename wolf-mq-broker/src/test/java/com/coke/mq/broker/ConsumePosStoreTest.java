package com.coke.mq.broker;

import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.store.ConsumeOffsetStore;
import com.coke.wolf.mq.broker.store.ConsumeQueue;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 9:22 下午
 */
public class ConsumePosStoreTest {

    private BrokerConfig brokerConfig = new BrokerConfig();

    @Test
    public void testPersist() {

        ConsumeQueue consumeQueue = new ConsumeQueue(brokerConfig);

        ConsumeOffsetStore consumeOffsetStore = new ConsumeOffsetStore(consumeQueue.getTopicTable());

        if (consumeOffsetStore != null) {

        }

    }
}
