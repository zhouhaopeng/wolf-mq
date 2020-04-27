package com.coke.wolf.mq.broker.store;

import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/19 10:22 上午
 */
public class CommitLogItemUtils {

    public static List<CommitLogItem> decode(byte[] bytes, int count) {

        List<CommitLogItem> commitLogItems = Lists.newArrayList();

        for (int i = 0; i < count; i++) {
            commitLogItems.add(decode(bytes));
        }

        return commitLogItems;
    }

    public static CommitLogItem decode(byte[] bytes) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        CommitLogItem commitLogItem = new CommitLogItem();

        int totalSize = byteBuffer.getInt();

        int topicSize = byteBuffer.getInt();
        byte[] topicBuf = new byte[topicSize];
        byteBuffer.get(topicBuf, 0, topicSize);

        int queueId = byteBuffer.getInt();

        int bodySize = byteBuffer.getInt();
        byte[] bodyBuf = new byte[bodySize];

        byteBuffer.get(bodyBuf, 0, bodySize);

        commitLogItem.setTopic(topicBuf);
        commitLogItem.setQueueId(queueId);
        commitLogItem.setBody(bodyBuf);

        return commitLogItem;
    }

    public static byte[] encode(CommitLogItem commitLogItem) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(commitLogItem.getTotalSize());

        byteBuffer.putInt(commitLogItem.getTotalSize());
        byteBuffer.putInt(commitLogItem.getTopicBytes().length);
        byteBuffer.put(commitLogItem.getTopicBytes());
        byteBuffer.putInt(commitLogItem.getQueueId());
        byteBuffer.putInt(commitLogItem.getBody().length);
        byteBuffer.put(commitLogItem.getBody());

        return byteBuffer.array();

    }
}
