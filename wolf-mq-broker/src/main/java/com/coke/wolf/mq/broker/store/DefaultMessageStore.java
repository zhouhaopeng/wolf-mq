package com.coke.wolf.mq.broker.store;

import com.coke.wolf.common.exception.WolfMqStoreException;
import com.coke.wolf.mq.broker.BrokerConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 5:27 下午
 */
public class DefaultMessageStore {

    private CommitLog commitLog;

    private ConsumeQueue consumeQueue;

    private final Map<String, TopicData> topicDataMap = Maps.newConcurrentMap();

    public DefaultMessageStore(BrokerConfig brokerConfig) {
        this.commitLog = new CommitLog(brokerConfig);
        this.consumeQueue = new ConsumeQueue(brokerConfig);

        init();
    }

    private void init() {
        Map<String, Map<Integer, MappedFileQueue>> topicTable = consumeQueue.getTopicTable();

        topicTable.forEach((k, v) -> {
            int queueNum = v.size();
            topicDataMap.putIfAbsent(k, new TopicData(k, queueNum, queueNum));
        });
    }

    public CommitLogItem getMessage(String topic, int queueId, long offset) {

        checkReadRange(topic, queueId);
        ConsumeQueueItem consumeQueueItem = consumeQueue.getConsumeQueueItem(topic, queueId, offset);

        if (consumeQueueItem != null) {
            return commitLog.getMessage(consumeQueueItem);
        }
        throw new WolfMqStoreException("msg not found");
    }

    public CommitLogItem getMessage(String topic, int queueId) {

        checkReadRange(topic, queueId);
        ConsumeQueueItem consumeQueueItem = consumeQueue.getConsumeQueueItem(topic, queueId);

        if (consumeQueueItem != null) {
            return commitLog.getMessage(consumeQueueItem);
        }
        throw new WolfMqStoreException("msg not found");
    }

    public List<CommitLogItem> getMessages(String topic, int queueId, int count) {

        final List<CommitLogItem> commitLogItems = Lists.newArrayList();
        checkReadRange(topic, queueId);
        List<ConsumeQueueItem> consumeQueueItems = consumeQueue.getConsumeQueueItem(topic, queueId, count);
        if (consumeQueueItems != null && !consumeQueueItems.isEmpty()) {
            consumeQueueItems.stream().forEach(consumeQueueItem ->
                commitLogItems.add(commitLog.getMessage(consumeQueueItem))
            );
            return commitLogItems;
        }
        throw new WolfMqStoreException("msg not found");
    }

    public void putMessage(CommitLogItem commitLogItem) {

        checkWriteRange(commitLogItem.getTopic(), commitLogItem.getQueueId());
        ConsumeQueueItem consumeQueueItem = commitLog.putMessage(commitLogItem);
        consumeQueue.putConsumeQueueItem(consumeQueueItem);
    }

    public void createTopic(String topic, int queueNums) {
        topicDataMap.putIfAbsent(topic, new TopicData(topic, queueNums, queueNums));
    }

    public List<TopicData> getTopic() {
        return Lists.newArrayList(topicDataMap.values());
    }

    private void checkReadRange(String topic, int queueId) {
        if (!topicDataMap.containsKey(topic) || topicDataMap.get(topic).getReadQueueNums() < queueId) {
            throw new WolfMqStoreException("queueId out of index!");
        }
    }

    private void checkWriteRange(String topic, int queueId) {
        if (!topicDataMap.containsKey(topic) || topicDataMap.get(topic).getWriteQueueNums() < queueId) {
            throw new WolfMqStoreException("queueId out of index!");
        }
    }
}
