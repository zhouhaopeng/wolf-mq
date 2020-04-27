package com.coke.wolf.mq.remote.netty.processor;

import com.coke.wolf.mq.remote.RemoteCommand;
import com.coke.wolf.mq.remote.enums.ProcessType;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 4:57 下午
 */
public class DefaultRequestProcessor implements NettyProcessor {

    private static final Logger logger = LogManager.getLogger(DefaultRequestProcessor.class);

    private Map<Object, NettyProcessor> processorMap = Maps.newHashMap();

    public void addProcessor(Object type, NettyProcessor processor) {
        processorMap.putIfAbsent(type, processor);
    }

    public NettyProcessor getProcessor(Object type) {
        NettyProcessor nettyProcessor = processorMap.get(type);
        if (nettyProcessor == null) {
            nettyProcessor = doNothingProcessor;
        }
        return nettyProcessor;
    }

    @Override public void processRemoteCommandRequest(ChannelHandlerContext ctx, RemoteCommand remoteCommand) {

        logger.info("receive request type " + remoteCommand.getType());
        getProcessor(ProcessType.convert(remoteCommand.getType())).processRemoteCommandRequest(ctx, remoteCommand);
    }

    private NettyProcessor doNothingProcessor = (c, r) -> {
        logger.warn("don't match any processor");
    };
}
