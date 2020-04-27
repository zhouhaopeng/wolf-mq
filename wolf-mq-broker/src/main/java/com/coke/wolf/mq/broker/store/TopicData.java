package com.coke.wolf.mq.broker.store;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 10:15 下午
 */
public class TopicData {

    private String topic;

    private int readQueueNums;

    private int writeQueueNums;

    public TopicData() {
    }

    public TopicData(String topic, int readQueueNums, int writeQueueNums) {
        this.topic = topic;
        this.readQueueNums = readQueueNums;
        this.writeQueueNums = writeQueueNums;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getReadQueueNums() {
        return readQueueNums;
    }

    public void setReadQueueNums(int readQueueNums) {
        this.readQueueNums = readQueueNums;
    }

    public int getWriteQueueNums() {
        return writeQueueNums;
    }

    public void setWriteQueueNums(int writeQueueNums) {
        this.writeQueueNums = writeQueueNums;
    }
}
