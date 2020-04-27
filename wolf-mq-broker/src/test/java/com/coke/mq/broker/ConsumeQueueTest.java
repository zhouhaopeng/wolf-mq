package com.coke.mq.broker;

import com.coke.wolf.mq.broker.BrokerConfig;
import com.coke.wolf.mq.broker.store.ConsumeQueue;
import com.coke.wolf.mq.broker.store.ConsumeQueueItem;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 11:07 上午
 */
public class ConsumeQueueTest {


    private BrokerConfig brokerConfig = new BrokerConfig();

    /**
     * { topic:[ 1:10 2:10 ] }
     */

    @Test
    public void test2() throws IOException {
        File file = new File("broker/consumeQueue/Test_Topci_One/1/0000000000");
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, BrokerConfig.INDEX_FILE_SIZE);

        while (true) {

            Long offset = mappedByteBuffer.getLong();
            Integer size = mappedByteBuffer.getInt();
            //System.out.println("[" + "offset=" + offset + ", " + "size=" + size + "]");
            if (offset <= 0) {
                break;
            }
            System.out.println("[" + "offset=" + offset + ", " + "size=" + size + "]");

        }
    }

    @Test
    public void putMessageTest() {

        ConsumeQueue queue = new ConsumeQueue(brokerConfig);
        ConsumeQueueItem item;
        for (int i = 1; i < 100; i++) {

            item = new ConsumeQueueItem(i + 1, i, "Test_Topci_One", i % 10);
            queue.putConsumeQueueItem(item);
        }

        queue.flush();
    }

    @Test
    public void testLoad() {
        ConsumeQueue queue = new ConsumeQueue(brokerConfig);


    }

    @Test
    public void getMessage() {
        ConsumeQueue queue = new ConsumeQueue(brokerConfig);

        //queue.load();

        for (int i = 0; i < 10; i++) {
            System.out.println("queueId==>"+i);
            for (int j = 0; j < 10; j++) {
               // ConsumeQueueItem consumeQueueItem = queue.getConsumeQueueItem("Test_Topci_One", i, j * BrokerConfig.INDEX_FILE_UNIT);
               // System.out.println(consumeQueueItem);
            }
        }

    }
}
