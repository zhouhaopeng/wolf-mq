package com.coke.wolf.mq.namesrv;

import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.common.model.store.BrokerData;
import com.coke.wolf.common.model.store.QueueData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 4:39 下午
 */
public class RouteInfoManager {

    private static final Logger logger = LogManager.getLogger(RouteInfoManager.class);

    private Map<String/*broker*/, BrokerData> brokerDataMap = Maps.newConcurrentMap();

    private Map<String/*topic*/, Map<String, QueueData>> topicTable = Maps.newConcurrentMap();

    public void saveBrokerData(String brokerName, BrokerData brokerData) {
        brokerDataMap.put(brokerName, brokerData);
    }

    public void saveTopicData(String topic, QueueData queueData) {

        logger.info("save topic info topic = " + topic + "," + GsonUtil.gsonString(queueData));

        Map<String, QueueData> queueDataMap = topicTable.get(topic);
        if (queueDataMap == null) {
            queueDataMap = Maps.newConcurrentMap();
            topicTable.put(topic, queueDataMap);
        }
        queueDataMap.putIfAbsent(queueData.getBrokerName(), queueData);
    }

    public Map<String,QueueData> queryTopicData(String topic) {
        return topicTable.get(topic);
    }

    public boolean containTopic(String topic) {
        return topicTable.containsKey(topic);
    }

    public BrokerData queryBrokerData(String brokerName) {
        return brokerDataMap.get(brokerName);
    }

}
