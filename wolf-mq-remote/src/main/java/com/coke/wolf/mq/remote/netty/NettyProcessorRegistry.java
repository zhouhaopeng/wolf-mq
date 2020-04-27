package com.coke.wolf.mq.remote.netty;

import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 5:18 下午
 */
public interface NettyProcessorRegistry {

    void register(DefaultRequestProcessor defaultRequestProcessor);

    default void register(DefaultRequestProcessor defaultRequestProcessor, Integer type,
        NettyProcessor nettyProcessor) {
        defaultRequestProcessor.addProcessor(type,nettyProcessor);
    }
}
