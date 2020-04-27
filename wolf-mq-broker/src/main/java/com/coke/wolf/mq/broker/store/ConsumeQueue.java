package com.coke.wolf.mq.broker.store;

import com.coke.wolf.common.exception.WolfMqStoreException;
import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.MappedFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 9:44 上午
 */
public class ConsumeQueue {

    private static final Logger logger = LogManager.getLogger(ConsumeQueue.class);

    private final String path;

    private final int fileSize;

    private final int fileItemUnit;

    private final Map<String /*topic*/, Map<Integer/*queueId*/, MappedFileQueue>> topicTable = Maps.newConcurrentMap();

    private final ConsumeOffsetStore consumeOffsetStore;

    public ConsumeQueue(BrokerConfig config) {
        this(config.CONSUME_QUEUE_PATH, config.INDEX_FILE_SIZE, config.INDEX_FILE_UNIT);
    }

    public ConsumeQueue(String path, int fileSize, int fileItemUnit) {
        this.path = path;
        this.fileSize = fileSize;
        this.fileItemUnit = fileItemUnit;

        load();

        consumeOffsetStore = new ConsumeOffsetStore(topicTable);
    }

    private MappedFileQueue findConsumeQueue(String topic, int queueId, boolean needCreate) {

        if ((!topicTable.containsKey(topic) || !topicTable.get(topic).containsKey(queueId)) && needCreate) {
            createQueue(topic, queueId);
        }
        checkRange(topic, queueId);
        return topicTable.get(topic).get(queueId);
    }

    private void createQueue(String topic, int queueId) {
        MappedFileQueue mappedFileQueue = new MappedFileQueue(fileSize, buildPath(topic, queueId));
        this.putMappedQueue(topic, queueId, mappedFileQueue);
    }

    private String buildPath(String topic, int queueId) {
        return path + File.separator + topic + File.separator + queueId;
    }

    public void putConsumeQueueItem(ConsumeQueueItem consumeQueueItem) {
        MappedFileQueue mappedFileQueue = findConsumeQueue(consumeQueueItem.getTopic(), consumeQueueItem.getQueueId(), true);

        consumeQueueItem.setTotalSize(fileItemUnit);
        byte[] content = ConsumeQueueItemUtils.encode(consumeQueueItem);
        mappedFileQueue.putContent(content);
    }

    private void checkRange(String topic, int queueId) {
        if (!topicTable.containsKey(topic) || !topicTable.get(topic).containsKey(queueId)) {
            throw new WolfMqStoreException(String.format("topic %s or queueId %s out of bound,please check!", topic, queueId));
        }
    }

    public ConsumeQueueItem getConsumeQueueItem(String topic, int queueId, long offset) {
        ConsumeQueueItem consumeQueueItem = null;

        MappedFileQueue mappedFileQueue = findConsumeQueue(topic, queueId, false);
        if (mappedFileQueue != null && getMaxCanReadCount(mappedFileQueue, fileItemUnit, 1) > 0) {
            byte[] bytes = mappedFileQueue.getContent(offset, fileItemUnit);
            consumeQueueItem = ConsumeQueueItemUtils.decode(bytes);
        }

        return consumeQueueItem;
    }

    public List<ConsumeQueueItem> getConsumeQueueItem(String topic, int queueId, int count) {
        final List<ConsumeQueueItem> consumeQueueItems = Lists.newArrayList();
        MappedFileQueue mappedFileQueue = findConsumeQueue(topic, queueId, false);
        if (mappedFileQueue != null && (count = getMaxCanReadCount(mappedFileQueue, fileItemUnit, count)) != 0) {

            byte[] bytes = mappedFileQueue.getContent(fileItemUnit * count);
            ByteBuffer byteBuf = ByteBuffer.wrap(bytes);
            IntStream.range(0, count).forEach(index -> {
                byte[] itemBuf = new byte[fileItemUnit];
                byteBuf.get(itemBuf);
                ConsumeQueueItem consumeQueueItem = ConsumeQueueItemUtils.decode(itemBuf);
                consumeQueueItems.add(consumeQueueItem);
            });
        }
        return consumeQueueItems;
    }

    private int getMaxCanReadCount(MappedFileQueue mappedFileQueue, int unit, int defaultCount) {

        long readLength = mappedFileQueue.getCanReadContentLength();
        int readCount = (int) (readLength / unit);
        return readCount <= defaultCount ? readCount : defaultCount;
    }

    public ConsumeQueueItem getConsumeQueueItem(String topic, int queueId) {
        ConsumeQueueItem consumeQueueItem = null;

        MappedFileQueue mappedFileQueue = findConsumeQueue(topic, queueId, false);
        if (mappedFileQueue != null) {
            byte[] bytes = mappedFileQueue.getContent(fileItemUnit);
            consumeQueueItem = ConsumeQueueItemUtils.decode(bytes);
        }

        return consumeQueueItem;
    }

    public void flush() {
        topicTable.values().stream().map(Lists::newArrayList).flatMap(List::stream).map(Map::values).map(Lists::newArrayList)
            .flatMap(List::stream).map(MappedFileQueue::getMappedFiles).flatMap(List::stream).forEach(MappedFile::flush);
    }

    private void load() {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(topicFile -> {
                    String topic = topicFile.getName();
                    File[] queueFiles = topicFile.listFiles();
                    if (queueFiles != null) {
                        Arrays.stream(queueFiles).forEach(queueFile -> {
                            loadQueue(topic, queueFile);
                        });
                    }
                });
            }
        }
    }

    private void loadQueue(String topic, File queueFile) {
        int queueId = Integer.parseInt(queueFile.getName());
        String queuePath = buildPath(topic, queueId);
        MappedFileQueue mappedFileQueue = new MappedFileQueue(fileSize, queuePath);

        this.putMappedQueue(topic, queueId, mappedFileQueue);
    }

    private void putMappedQueue(String topic, int queueId, MappedFileQueue mappedFileQueue) {
        Map<Integer, MappedFileQueue> queueTable = topicTable.containsKey(topic) ? topicTable.get(topic) : Maps.newConcurrentMap();
        queueTable.putIfAbsent(queueId, mappedFileQueue);
        topicTable.putIfAbsent(topic, queueTable);
    }

    public Map<String, Map<Integer, MappedFileQueue>> getTopicTable() {
        return topicTable;
    }
}
