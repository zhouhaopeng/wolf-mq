package com.coke.wolf.common.model.client;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 2:06 下午
 */
public class ClientSendMsgRequest {

    private String topic;

    private int queueId;

    private byte[] body;

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
