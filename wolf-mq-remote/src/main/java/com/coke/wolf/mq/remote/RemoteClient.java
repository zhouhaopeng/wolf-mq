package com.coke.wolf.mq.remote;

import io.netty.channel.Channel;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 10:08 下午
 */
public interface RemoteClient {

    Channel connect(String address,int port);
}
