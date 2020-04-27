package com.coke.mq.broker;

import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.store.CommitLog;
import com.coke.wolf.mq.broker.store.CommitLogItem;
import com.coke.wolf.mq.broker.store.CommitLogItemUtils;
import com.coke.wolf.mq.broker.store.ConsumeQueue;
import com.coke.wolf.mq.broker.store.ConsumeQueueItem;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/19 10:46 上午
 */
public class CommitLogTet {

    private String topic = "Test_Topci_One";

    private int topicSize = topic.getBytes(Charset.forName("UTF-8")).length;

    private String body = "Hello mq";

    private int bodySize = body.getBytes(Charset.forName("UTF-8")).length;

    private BrokerConfig brokerConfig = new BrokerConfig();

    @Test
    public void putMsg() {

        CommitLog commitLog = new CommitLog(brokerConfig);

        for (int i = 0; i < 10; i++) {

            CommitLogItem commitLogItem = new CommitLogItem();

            commitLogItem.setTopic(topic);
            commitLogItem.setQueueId(i);
            commitLogItem.setBody(body.getBytes(Charset.forName("UTF-8")));

            commitLog.putMessage(commitLogItem);
        }

    }

    @Test
    public void test2() throws IOException {
        File file = new File("broker/commitLog/00000000000000000000000000");
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, BrokerConfig.MSG_FILE_SIZE);

        long size = 0;
        long count = 0;
        while (true) {

            int totalSize = mappedByteBuffer.getInt();
            if (totalSize <= 0) {
                break;
            }
            byte[] total = new byte[totalSize];
            mappedByteBuffer.position(mappedByteBuffer.position() - 4);
            mappedByteBuffer.get(total, 0, totalSize);
            size += totalSize;
            CommitLogItem commitLogItem = CommitLogItemUtils.decode(total);

           // System.out.println(commitLogItem);
            count ++;
        }
        System.out.println(size);
        System.out.println(size/(2<<20));
        System.out.println(count);
    }


    @Test
    public void testLoad() {

//        CommitLog commitLog = new CommitLog(brokerConfig);
//
//        ConsumeQueue consumeQueue = new ConsumeQueue(brokerConfig);
//
//        for (int i = 0; i < 10; i++) {
//            System.out.println("queueId==>" + i);
//            ConsumeQueueItem consumeQueueItem = consumeQueue.getConsumeQueueItem(topic, i, 0 * BrokerConfig.INDEX_FILE_UNIT);
//            System.out.println(consumeQueueItem);
//
//            CommitLogItem commitLogItem = commitLog.getMessage(consumeQueueItem);
//            System.out.println(commitLogItem+" ,body="+new String(commitLogItem.getBody(), Charset.forName("UTF-8")));
//        }
    }
}
