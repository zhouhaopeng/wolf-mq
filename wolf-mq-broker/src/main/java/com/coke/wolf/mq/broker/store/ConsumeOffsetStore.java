package com.coke.wolf.mq.broker.store;

import com.coke.wolf.common.exception.WolfMqStoreException;
import com.coke.wolf.common.utils.FileUtils;
import com.coke.wolf.common.utils.GsonUtil;
import com.coke.wolf.mq.broker.BrokerConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/20 7:31 下午
 */
public class ConsumeOffsetStore extends Thread {

    private static final Logger logger = LogManager.getLogger(ConsumeOffsetStore.class);

    private final String path;

    private final String fileName;

    private Map<String, Map<Integer, MappedFileQueue>> topicTable;

    public ConsumeOffsetStore(Map<String /*topic*/, Map<Integer/*queueId*/, MappedFileQueue>> topicTable) {
        this(BrokerConfig.TOPIC_FILE_PATH, BrokerConfig.TOPIC_FILE_NAME, topicTable);
    }

    public ConsumeOffsetStore(String path, String fileName,
        Map<String /*topic*/, Map<Integer/*queueId*/, MappedFileQueue>> topicTable) {
        this.path = path;
        this.fileName = fileName;
        this.topicTable = topicTable;

        FileUtils.ensureDirOK(path);

        load();

        Runtime.getRuntime().addShutdownHook(this);
    }

    private void load() {
        deserialize();
    }

    @Override public void run() {

        if (!topicTable.isEmpty()) {
            Map<String, List<ConsumeOffsetItem>> consumePosTable = Maps.newHashMap();

            topicTable.forEach((k, v) -> {

                if (!v.isEmpty()) {

                    List<ConsumeOffsetItem> consumePosItems = Lists.newArrayList();
                    consumePosTable.put(k, consumePosItems);
                    v.forEach((queueId, mappedQueue) -> {
                        consumePosItems.add(new ConsumeOffsetItem(queueId, mappedQueue.getReadOffset(), mappedQueue.getWroteOffset()));
                    });
                }

            });
            persist(consumePosTable);

        }
    }

    private void deserialize() {
        try {
            String filePath = buildPath();
            if (!FileUtils.exist(filePath)) {
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("UTF-8")));
            String line = bufferedReader.readLine();
            if (line != null) {

                Map<String, List<ConsumeOffsetItem>> consumePosTable = GsonUtil.gsonToMap(line, new TypeToken<Map<String, List<ConsumeOffsetItem>>>() {
                });

                if (!consumePosTable.isEmpty()) {

                    topicTable.forEach((k, v) -> {
                        List<ConsumeOffsetItem> consumePosItems = consumePosTable.get(k);
                        if (!consumePosItems.isEmpty()) {

                            Map<Integer, ConsumeOffsetItem> consumePosItemMap = consumePosItems.stream().collect(Collectors.toMap(ConsumeOffsetItem::getQueueId, o -> o, (k1, k2) -> k1));
                            v.forEach((queueId, mappedQueue) -> {
                                ConsumeOffsetItem consumePosItem = consumePosItemMap.get(queueId);
                                if (consumePosItem != null) {
                                    mappedQueue.setReadOffset(consumePosItem.getReadPos());
                                    mappedQueue.setWroteOffset(consumePosItem.getWrotePos());
                                }

                            });

                        }
                    });
                }
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }

        } catch (IOException e) {
            logger.error("deserialize consumePos error !");
            throw new WolfMqStoreException("persist consumePos error !", e);
        }
    }

    private void persist(Map<String, List<ConsumeOffsetItem>> consumePosTable) {
        String gson = "";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(buildPath())), Charset.forName("UTF-8")));
            gson = GsonUtil.gsonString(consumePosTable);
            bufferedWriter.write(gson);
            bufferedWriter.flush();
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

        } catch (IOException e) {
            logger.error("persist consumePos error ! consumePosTable " + gson);
            throw new WolfMqStoreException("persist consumePos error !", e);
        }

    }

    private File getFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            file = new File(path);
        }

        return file;
    }

    private String buildPath() {

        return path + File.separator + fileName;
    }

    static class ConsumeOffsetItem {

        private Integer queueId;
        private Long readPos;
        private Long wrotePos;

        public ConsumeOffsetItem() {
        }

        public ConsumeOffsetItem(Integer queueId, Long readPos, Long wrotePos) {
            this.queueId = queueId;
            this.readPos = readPos;
            this.wrotePos = wrotePos;
        }

        public Integer getQueueId() {
            return queueId;
        }

        public void setQueueId(Integer queueId) {
            this.queueId = queueId;
        }

        public Long getReadPos() {
            return readPos;
        }

        public void setReadPos(Long readPos) {
            this.readPos = readPos;
        }

        public Long getWrotePos() {
            return wrotePos;
        }

        public void setWrotePos(Long wrotePos) {
            this.wrotePos = wrotePos;
        }
    }
}
