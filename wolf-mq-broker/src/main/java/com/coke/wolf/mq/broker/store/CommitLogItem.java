package com.coke.wolf.mq.broker.store;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/19 9:56 上午
 */
public class CommitLogItem {

    public static final Charset charset = Charset.forName("UTF-8");

    public static final int FIX_SIZE = 16;

    private String topic;

    private int queueId;

    private byte[] body;
    /*不序列化*/
    private long offset;

    public CommitLogItem() {
    }

    public CommitLogItem(String topic, int queueId, byte[] body) {
        this.topic = topic;
        this.queueId = queueId;
        this.body = body;
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getTopicBytes() {
        return topic.getBytes(charset);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTopic(byte[] topic) {
        this.topic = new String(topic, charset);
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getTotalSize() {
        return FIX_SIZE + getTopicBytes().length + getBody().length;
    }

    @Override public String toString() {
        return "CommitLogItem{" +
            "topic='" + topic + '\'' +
            ", queueId=" + queueId +
            ", body=" + Arrays.toString(body) +
            ", offset=" + offset +
            '}';
    }
}
