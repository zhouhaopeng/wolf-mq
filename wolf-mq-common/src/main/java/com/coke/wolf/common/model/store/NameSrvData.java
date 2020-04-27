package com.coke.wolf.common.model.store;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 7:26 下午
 */
public class NameSrvData {

    private String addr;

    private int port;

    public NameSrvData(String addr, int port) {
        this.addr = addr;
        this.port = port;
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
}
