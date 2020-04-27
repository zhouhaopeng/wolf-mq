package com.coke.wolf.mq.client;

import com.coke.wolf.mq.client.manger.RouteInfoManager;
import java.io.IOException;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 10:32 下午
 */
public class RouteInfoManagerTest {

    private String topic = "Test_Topci_One";

    private RouteInfoManager routeInfoManager;

    @Test
    public void fetchTopic() throws IOException {

        routeInfoManager = new RouteInfoManager();

        routeInfoManager.subscribe("127.0.0.1:9999",topic);

        System.in.read();
    }
}
