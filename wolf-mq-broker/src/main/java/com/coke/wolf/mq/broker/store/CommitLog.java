package com.coke.wolf.mq.broker.store;

import com.coke.wolf.mq.broker.BrokerConfig;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/19 9:55 上午
 */
public class CommitLog {

    private final MappedFileQueue mappedFileQueue;

    public CommitLog(BrokerConfig config) {
        this(config.COMMIT_LOG_PATH, config.MSG_FILE_SIZE);
    }

    public CommitLog(String path, int fileSize) {

        this.mappedFileQueue = new MappedFileQueue(fileSize, path);
    }

    public ConsumeQueueItem putMessage(CommitLogItem commitLogItem) {

        long offset = mappedFileQueue.putContent(CommitLogItemUtils.encode(commitLogItem));
        commitLogItem.setOffset(offset);
        return ConsumeQueueItemUtils.convert(commitLogItem);
    }

    public CommitLogItem getMessage(ConsumeQueueItem consumeQueueItem) {

        CommitLogItem commitLogItem = null;

        long offset = consumeQueueItem.getOffset();
        int size = consumeQueueItem.getSize();
        byte[] bytes = mappedFileQueue.getContent(offset, size);

        commitLogItem = CommitLogItemUtils.decode(bytes);

        return commitLogItem;
    }
}
