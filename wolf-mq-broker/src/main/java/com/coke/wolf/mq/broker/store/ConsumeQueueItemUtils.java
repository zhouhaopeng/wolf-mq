package com.coke.wolf.mq.broker.store;

import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 10:40 上午
 */
public class ConsumeQueueItemUtils {

    public static List<ConsumeQueueItem> decode(byte[] bytes, int count) {

        List<ConsumeQueueItem> consumeQueueItems = Lists.newArrayList();

        for (int i = 0; i < count; i++) {
            consumeQueueItems.add(decode(bytes));
        }

        return consumeQueueItems;
    }

    public static ConsumeQueueItem decode(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        ConsumeQueueItem item = new ConsumeQueueItem();
        item.setTotalSize(byteBuffer.getInt());
        item.setOffset(byteBuffer.getLong());
        item.setSize(byteBuffer.getInt());
        return item;
    }

    public static byte[] encode(ConsumeQueueItem consumeQueueItem) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(consumeQueueItem.getTotalSize());
        byteBuffer.putInt(consumeQueueItem.getTotalSize());
        byteBuffer.putLong(consumeQueueItem.getOffset());
        byteBuffer.putInt(consumeQueueItem.getSize());
        return byteBuffer.array();

    }

    public static ConsumeQueueItem convert(CommitLogItem commitLogItem) {

        ConsumeQueueItem item = new ConsumeQueueItem();
        item.setOffset(commitLogItem.getOffset());
        item.setSize(commitLogItem.getTotalSize());
        item.setTopic(commitLogItem.getTopic());
        item.setQueueId(commitLogItem.getQueueId());
        return item;
    }
}
