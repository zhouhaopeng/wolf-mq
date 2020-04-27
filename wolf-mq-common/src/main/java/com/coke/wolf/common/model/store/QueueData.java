package com.coke.wolf.common.model.store;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 4:30 下午
 */
public class QueueData implements Comparable<QueueData> {

    private String brokerName;

    private String topic;

    private int readQueueNums;

    private int writeQueueNums;

    private int perm;

    public QueueData() {
    }

    public QueueData(String brokerName, String topic, int readQueueNums, int writeQueueNums, int perm) {
        this.brokerName = brokerName;
        this.topic = topic;
        this.readQueueNums = readQueueNums;
        this.writeQueueNums = writeQueueNums;
        this.perm = perm;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
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

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }

    @Override public int compareTo(QueueData o) {
        int i = this.brokerName.compareTo(o.getBrokerName());
        if (i == 0) {
            i = this.topic.compareTo(o.getTopic());
        }
        return i;
    }
}
