package com.coke.wolf.mq.broker;

import java.util.EventListener;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 7:42 下午
 */
public interface InvListener extends EventListener {

    void listen(InvEvent event);
}
