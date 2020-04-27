package com.coke.wolf.common.model.broker;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/27 8:13 下午
 */
public class BrokerPullMsgDTO {

    private String topic;

    private int queueId;

    private byte[] body;

    public BrokerPullMsgDTO() {
    }

    public BrokerPullMsgDTO(String topic, int queueId, byte[] body) {
        this.topic = topic;
        this.queueId = queueId;
        this.body = body;
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

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
