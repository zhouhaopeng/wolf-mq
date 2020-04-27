package com.coke.wolf.common.model.broker;

import java.util.Map;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 5:13 下午
 */
public class BrokerRegisterRequest {

    private String brokerName;

    private String address;

    private int port;

    private Map<String, QueueDataRequest> queueDataRequestMap;

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, QueueDataRequest> getQueueDataRequestMap() {
        return queueDataRequestMap;
    }

    public void setQueueDataRequestMap(
        Map<String, QueueDataRequest> queueDataRequestMap) {
        this.queueDataRequestMap = queueDataRequestMap;
    }

   public static class QueueDataRequest {

        private String topic;

        private int readQueueNums;

        private int writeQueueNums;

        private int perm;

        public QueueDataRequest() {
        }

        public QueueDataRequest(String topic, int readQueueNums, int writeQueueNums) {
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

        public int getPerm() {
            return perm;
        }

        public void setPerm(int perm) {
            this.perm = perm;
        }
    }
}

