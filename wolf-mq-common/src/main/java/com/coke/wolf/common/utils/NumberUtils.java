package com.coke.wolf.common.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 11:59 上午
 */
public class NumberUtils {

    private static final Random random = new Random();

    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }

    public static String longToByteStr(long x) {
        byte[] bytes = longToBytes(x);
        return Arrays.toString(bytes);
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }

    public static int random(int key) {
        return random.nextInt(key);
    }
}
