package com.coke.wolf.mq.client.model;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 10:41 上午
 */
public class Message {

    private String topic;

    private byte[] body;

    private int queueId = -1;

    public Message() {
    }

    public Message(String topic, byte[] body) {
        this.topic = topic;
        this.body = body;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }
}
