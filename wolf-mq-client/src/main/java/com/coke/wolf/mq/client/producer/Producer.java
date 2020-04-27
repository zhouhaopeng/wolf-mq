package com.coke.wolf.mq.client.producer;

import com.coke.wolf.mq.client.model.Message;
import com.coke.wolf.mq.client.model.SendResult;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 10:40 上午
 */
public interface Producer {

    SendResult send(Message message);

    void setNameSrv(String addr);

    void start();
}
