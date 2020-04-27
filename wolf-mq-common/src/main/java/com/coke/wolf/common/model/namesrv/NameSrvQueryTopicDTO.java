package com.coke.wolf.common.model.namesrv;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 4:25 下午
 */
public class NameSrvQueryTopicDTO {

    private String brokerName;

    private String addr;

    private int port;

    private String topic;

    private int readQueueNums;

    private int writeQueueNums;

    private int perm;

    public NameSrvQueryTopicDTO() {
    }

    public NameSrvQueryTopicDTO(String brokerName, String addr, int port, String topic, int readQueueNums,
        int writeQueueNums, int perm) {
        this.brokerName = brokerName;
        this.addr = addr;
        this.port = port;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
