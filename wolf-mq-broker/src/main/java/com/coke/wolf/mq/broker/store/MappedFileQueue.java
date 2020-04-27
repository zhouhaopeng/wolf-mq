package com.coke.wolf.mq.broker.store;

import com.coke.wolf.common.exception.WolfMqStoreException;
import com.coke.wolf.mq.broker.MappedFile;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 8:39 上午
 */
public class MappedFileQueue {

    private static final Logger logger = LogManager.getLogger(MappedFileQueue.class);

    private final int mappedFileSize;

    private final String path;

    private long maxOffset = 0L;

    private long flushOffset = 0L;

    private AtomicLong wroteOffset = new AtomicLong(0L);

    private AtomicLong readOffset = new AtomicLong(0L);

    private MappedFile lastMappedFile;

    //读写锁
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    //获取读锁
    private final Lock rLock = rwl.readLock();
    //获取写锁
    private final Lock wLock = rwl.writeLock();

    private final CopyOnWriteArrayList<MappedFile> mappedFiles = new CopyOnWriteArrayList<MappedFile>();

    public MappedFileQueue(int mappedFileSize, String path) {
        this.mappedFileSize = mappedFileSize;
        this.path = path;

        load();
    }

    private void checkRange(final long offset) {
        if (offset > wroteOffset.get()) {
            throw new WolfMqStoreException(String.format("offset %s out of bound,please check!", offset));
        }
    }

    private MappedFile selectMappedFile(final long offset) {

        checkRange(offset);
        int index = (int) (offset / mappedFileSize);
        return mappedFiles.get(index);
    }

    private MappedFile getLastMappedFile() {
        MappedFile mappedFile = lastMappedFile;
        try {
            int relativeWroteOffset = getRelativeOffset(wroteOffset.get());
            if (mappedFile == null || mappedFile.isFull(relativeWroteOffset)) {
                createMappedFile(mappedFiles.size());
            }
            mappedFile = lastMappedFile;
        } catch (IOException e) {
            logger.error("create mapped file error", e);
            throw new WolfMqStoreException("create mapped file error!");
        }
        return mappedFile;

    }

    private void createMappedFile(int index) throws IOException {
        lastMappedFile = new DefaultMappedFile(path, index, mappedFileSize);
        this.mappedFiles.addIfAbsent(lastMappedFile);
        maxOffset += mappedFileSize;
    }

    public CopyOnWriteArrayList<MappedFile> getMappedFiles() {
        return mappedFiles;
    }

    private int getRelativeOffset(final long offset) {
        return (int) (offset % mappedFileSize);
    }

    public long putContent(byte[] content) {

        long offset;
        try {
            wLock.lock();

            offset = wroteOffset.get();
            MappedFile mappedFile = this.getLastMappedFile();
            mappedFile.appendContent(content, getRelativeOffset(offset));
            wroteOffset.getAndAdd(content.length);

        } finally {
            wLock.unlock();
        }

        return offset;
    }

    public byte[] getContent(final long offset, int size) {

        int relativeWroteOffset = getRelativeOffset(offset);
        MappedFile mappedFile = this.selectMappedFile(offset);
        byte[] bytes = mappedFile.fetchContent(relativeWroteOffset, size);

        return bytes;
    }

    public byte[] getContent(int size) {

        byte[] bytes;
        try {
            rLock.lock();
            long offset = readOffset.getAndAdd(size);
            bytes = getContent(offset, size);

        } finally {
            rLock.unlock();
        }
        return bytes;
    }

    public long getCanReadContentLength() {
        return this.wroteOffset.get() - this.readOffset.get();
    }

    public void load() {
        File file = new File(path);
        String[] subFileNames = file.list();
        if (!ArrayUtils.isEmpty(subFileNames)) {
            try {
                List<String> subFileNameList = Arrays.stream(subFileNames).sorted(Comparator.comparing(f -> Long.parseLong(f))).collect(Collectors.toList());

                int index = 0;
                for (; index < subFileNameList.size(); index++) {
                    MappedFile mappedFile = new DefaultMappedFile(path, index, mappedFileSize);
                    mappedFiles.add(index, mappedFile);

                }

                MappedFile mappedFile = this.mappedFiles.get(index - 1);
                ByteBuffer byteBuffer = mappedFile.content();
                int relativePos = 0;
                while (true) {
                    byteBuffer.position(relativePos);
                    int totalSize = byteBuffer.getInt();
                    if (totalSize <= 0) {
                        break;
                    }
                    relativePos += totalSize;
                }

                lastMappedFile = mappedFile;

                this.maxOffset = subFileNameList.size() * (long) mappedFileSize;
                long wroteOffset = (subFileNameList.size() - 1) * (long) mappedFileSize + relativePos;
                this.wroteOffset.set(wroteOffset);
            } catch (IOException e) {
                logger.error("load mapped file error", e);
                throw new WolfMqStoreException("create mapped file error!");
            }

        }

    }

    public long getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(long maxOffset) {
        this.maxOffset = maxOffset;
    }

    public long getFlushOffset() {
        return flushOffset;
    }

    public void setFlushOffset(long flushOffset) {
        this.flushOffset = flushOffset;
    }

    public long getWroteOffset() {
        return wroteOffset.get();
    }

    public void setWroteOffset(long wroteOffset) {
        this.wroteOffset.set(wroteOffset);
    }

    public long getReadOffset() {
        return readOffset.get();
    }

    public void setReadOffset(long readOffset) {
        this.readOffset.set(readOffset);
    }
}
