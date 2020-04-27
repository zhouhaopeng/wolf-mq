package com.coke.wolf.mq.broker;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 9:46 上午
 */
public class BrokerConfig {

    public static final String ROOT_PATH = "broker";

    public static final String CONSUME_QUEUE_PATH = ROOT_PATH + File.separator + "consumeQueue";

    public static final String COMMIT_LOG_PATH = ROOT_PATH + File.separator + "commitLog";

    public static final String TOPIC_FILE_PATH = ROOT_PATH + File.separator + "topic";

    public static final String TOPIC_FILE_NAME = "topic.json";

    public static final int MSG_FILE_SIZE = 1 << 30;

    public static final int INDEX_FILE_SIZE = 6 * (1 << 20);

    public static final int INDEX_FILE_UNIT = 16;

    public static final int BROKER_PORT = 9998;

    public static String getLocalHost() {
        String host = null;
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            host = ip4.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }

}
