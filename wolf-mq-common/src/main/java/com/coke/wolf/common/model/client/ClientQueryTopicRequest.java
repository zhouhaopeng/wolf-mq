package com.coke.wolf.common.model.client;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 4:21 下午
 */
public class ClientQueryTopicRequest {

    private String topic;

    public ClientQueryTopicRequest() {
    }

    public ClientQueryTopicRequest(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
