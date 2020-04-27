package com.coke.wolf.mq.broker.store;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 10:03 上午
 */
public class ConsumeQueueItem {

    private int totalSize;

    private long offset;

    private int size;
    /*不序列化*/
    private String topic;
    /*不序列化*/
    private int queueId;

    public ConsumeQueueItem() {
    }

    public ConsumeQueueItem(long offset, int size, String topic, int queueId) {
        this.offset = offset;
        this.size = size;
        this.topic = topic;
        this.queueId = queueId;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    @Override public String toString() {
        return "ConsumeQueueItem{" +
            "offset=" + offset +
            ", size=" + size +
            ", topic='" + topic + '\'' +
            ", queueId=" + queueId +
            '}';
    }
}
