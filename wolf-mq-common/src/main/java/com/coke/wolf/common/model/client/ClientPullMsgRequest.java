package com.coke.wolf.common.model.client;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/27 6:43 下午
 */
public class ClientPullMsgRequest {

    private String topic;

    private int queueId = -1;

    private int maxCount;

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

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
