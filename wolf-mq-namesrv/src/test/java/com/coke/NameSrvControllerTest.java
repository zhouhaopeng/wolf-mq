package com.coke;

import com.coke.wolf.mq.namesrv.NameSrvController;
import java.io.IOException;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 10:51 下午
 */
public class NameSrvControllerTest {

    @Test
    public void start() throws IOException {
        NameSrvController nameSrvController = new NameSrvController();

        nameSrvController.start();
        System.in.read();
    }
}
