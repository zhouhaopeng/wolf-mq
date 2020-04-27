package com.coke.wolf.mq.broker;

import com.coke.wolf.common.enums.InvEventType;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 7:38 下午
 */
public interface InvEvent {

    InvEventType type();
}
