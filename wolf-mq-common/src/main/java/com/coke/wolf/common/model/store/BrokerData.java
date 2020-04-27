package com.coke.wolf.common.model.store;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 4:16 下午
 */
public class BrokerData implements Comparable<BrokerData> {

    private String name;

    private String address;

    private int port;

    public BrokerData() {
    }

    public BrokerData(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override public int compareTo(BrokerData o) {
        return this.name.compareTo(o.getName());
    }
}
