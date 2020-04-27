package com.coke.wolf.mq.broker;

import java.nio.ByteBuffer;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 7:15 上午
 */
public interface MappedFile extends Comparable {

    String name();

    int size();

    long minOffset();

    long maxOffset();

    ByteBuffer content();

    void appendContent(byte[] content, int offset);

    byte[] fetchContent(int offset, int size);

    boolean contain(int offset, int size);

    int index();

    void flush();

    boolean isFull(int wroteOffset);
}
