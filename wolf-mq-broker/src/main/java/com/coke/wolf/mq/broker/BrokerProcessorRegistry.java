package com.coke.wolf.mq.broker;

import com.coke.wolf.mq.broker.processor.ClientRequestProcessor;
import com.coke.wolf.mq.remote.enums.ProcessType;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 5:35 下午
 */
public class BrokerProcessorRegistry {

    private BrokerController brokerController;

    private ClientRequestProcessor clientRequestProcessor;

    public BrokerProcessorRegistry(BrokerController brokerController) {
        this.brokerController = brokerController;

        initProcessor();
    }

    private void initProcessor() {
        clientRequestProcessor = new ClientRequestProcessor(brokerController);
    }

    public void register() {

        DefaultRequestProcessor defaultRequestProcessor = brokerController.getDefaultRequestProcessor();
        addProcessor(defaultRequestProcessor, ProcessType.Client_Request, clientRequestProcessor);
    }

    private void addProcessor(DefaultRequestProcessor defaultRequestProcessor, ProcessType type,
        NettyProcessor nettyProcessor) {
        defaultRequestProcessor.addProcessor(type, nettyProcessor);
    }

}
