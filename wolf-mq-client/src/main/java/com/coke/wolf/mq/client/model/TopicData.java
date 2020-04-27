package com.coke.wolf.mq.client.model;

import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.model.store.QueueData;
import com.coke.wolf.common.utils.NumberUtils;
import io.netty.channel.Channel;
import java.util.List;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/26 10:02 下午
 */
public class TopicData {

    private String topic;

    private BrokerData brokerData;

    private QueueData queueData;

    private List<Channel> channels;

    public TopicData() {
    }

    public TopicData(String topic, BrokerData brokerData, QueueData queueData) {
        this.topic = topic;
        this.brokerData = brokerData;
        this.queueData = queueData;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public BrokerData getBrokerData() {
        return brokerData;
    }

    public void setBrokerData(BrokerData brokerData) {
        this.brokerData = brokerData;
    }

    public QueueData getQueueData() {
        return queueData;
    }

    public void setQueueData(QueueData queueData) {
        this.queueData = queueData;
    }

    public Channel getChannel() {
        return channels.get(NumberUtils.random(10));
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public int randomQueueId(int queueId) {
        if (queueId >= 0 && queueId <= queueData.getWriteQueueNums()) {
            return queueId;
        }
        return NumberUtils.random(queueData.getWriteQueueNums());
    }
}
