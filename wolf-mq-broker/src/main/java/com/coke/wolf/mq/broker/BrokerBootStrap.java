package com.coke.wolf.mq.broker;

import java.io.IOException;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 10:46 下午
 */
public class BrokerBootStrap {

    private static String topic = "Test_Topci_One";

    public static void main(String[] args) throws IOException {
        BrokerController brokerController = new BrokerController("broker-a", "127.0.0.1", 9999);
        brokerController.getDefaultMessageStore().createTopic(topic, 16);
        brokerController.start();
        System.in.read();
    }
}
