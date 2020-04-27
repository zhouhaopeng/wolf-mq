package com.coke.wolf.mq.broker.store;

import com.coke.wolf.mq.broker.MappedFile;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 7:21 上午
 */
public abstract class AbstractMappedFile implements MappedFile {

    private String name;

    private int size;

    private long minOffset;

    private long maxOffset;

    private int index;

    public AbstractMappedFile(String name, int index, int size) {
        this.name = name;
        this.size = size;
        this.index = index;

        this.minOffset = index * (long) size;
        this.maxOffset = minOffset + size;
    }

    @Override public void appendContent(byte[] content, int offset) {

        ByteBuffer byteBuffer = content();
        byteBuffer.position(offset);
        byteBuffer.put(content);
    }

    @Override public byte[] fetchContent(int offset, int size) {

        byte[] bytes = new byte[size];
        ByteBuffer byteBuffer = content();
        byteBuffer.position(offset);
        byteBuffer.get(bytes);

        return bytes;
    }

    @Override public boolean contain(int offset, int size) {
        return this.minOffset >= offset && offset + size <= this.maxOffset;
    }

    @Override public void flush() {

    }

    @Override public boolean isFull(int wroteOffset) {
        return wroteOffset >= maxOffset;
    }

    @Override public String name() {
        return name;
    }

    @Override public int size() {
        return size;
    }

    @Override public long minOffset() {
        return minOffset;
    }

    @Override public long maxOffset() {
        return maxOffset;
    }

    @Override public int index() {
        return index;
    }

    @Override public int compareTo(Object o) {
        MappedFile other = (MappedFile) o;
        return this.name.compareTo(other.name());
    }

}
