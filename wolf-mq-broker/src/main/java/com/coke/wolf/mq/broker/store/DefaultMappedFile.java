package com.coke.wolf.mq.broker.store;

import com.coke.wolf.common.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 7:18 上午
 */
public class DefaultMappedFile extends AbstractMappedFile {

    private static final Logger logger = LogManager.getLogger(DefaultMappedFile.class);

    private MappedByteBuffer mappedByteBuffer;

    private File file;
    private FileChannel fileChannel;

    public DefaultMappedFile(String path, int index, int size) throws IOException {
        super(path, index, size);

        String fileName = path + File.separator + String.format("%026d",minOffset());
        file = new File(fileName);
        FileUtils.ensureDirOK(file.getParent());
        init();
    }

    private void init() throws IOException {

        boolean ok = false;
        try {
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, size());
            ok = true;
        } catch (FileNotFoundException e) {
            logger.error("Failed to create file " + this.name(), e);
            throw e;
        } catch (IOException e) {
            logger.error("Failed to map file " + this.name(), e);
            throw e;
        } finally {
            if (!ok && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }

    @Override public ByteBuffer content() {
        return mappedByteBuffer;
    }

    @Override public void flush() {
        this.mappedByteBuffer.force();
    }
}
