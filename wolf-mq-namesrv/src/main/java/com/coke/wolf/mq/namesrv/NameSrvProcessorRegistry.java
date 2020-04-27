package com.coke.wolf.mq.namesrv;

import com.coke.wolf.mq.namesrv.processor.BrokerRequestProcessor;
import com.coke.wolf.mq.namesrv.processor.ClientRequestProcessor;
import com.coke.wolf.mq.remote.enums.ProcessType;
import com.coke.wolf.mq.remote.netty.processor.DefaultRequestProcessor;
import com.coke.wolf.mq.remote.netty.processor.NettyProcessor;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 5:35 下午
 */
public class NameSrvProcessorRegistry {

    private NameSrvController nameSrvController;

    private BrokerRequestProcessor brokerRequestProcessor;

    private ClientRequestProcessor clientRequestProcessor;

    public NameSrvProcessorRegistry(NameSrvController nameSrvController) {
        this.nameSrvController = nameSrvController;

        initProcessor();
    }

    private void initProcessor() {
        brokerRequestProcessor = new BrokerRequestProcessor(nameSrvController);
        clientRequestProcessor = new ClientRequestProcessor(nameSrvController);
    }

    public void register() {

        DefaultRequestProcessor defaultRequestProcessor = nameSrvController.getDefaultRequestProcessor();

        addProcessor(defaultRequestProcessor, ProcessType.Broker_Request, brokerRequestProcessor);
        addProcessor(defaultRequestProcessor, ProcessType.Client_Request, clientRequestProcessor);
    }

    private void addProcessor(DefaultRequestProcessor defaultRequestProcessor, ProcessType type,
        NettyProcessor nettyProcessor) {
        defaultRequestProcessor.addProcessor(type, nettyProcessor);
    }

}
