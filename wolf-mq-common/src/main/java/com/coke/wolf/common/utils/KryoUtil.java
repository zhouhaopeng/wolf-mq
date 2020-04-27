package com.coke.wolf.common.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KryoUtil {

    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            // configure kryo instance, customize settings
            kryo.setReferences(false);
            kryo.register(Collection.class);
            kryo.register(Map.class);
            return kryo;
        }
    };
    private static Map<Class, Registration> registrationMap = new ConcurrentHashMap<Class, Registration>();

    /**
     * 序列化
     *
     * @param obj 序列化对象
     * @return 序列化后的byte[]值
     */
    public static <T> byte[] serializer(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Kryo kryo = kryos.get();
        if (!registrationMap.containsKey(clazz)) {
            Registration registration = kryo.register(clazz);
            registrationMap.put(clazz, registration);
        }
        ByteArrayOutputStream outputStream = null;
        Output output = null;
        byte[] bytes;
        try {
            outputStream = new ByteArrayOutputStream();
            output = new Output(outputStream);
            kryo.writeObject(output, obj);
            output.flush();
            bytes = outputStream.toByteArray();
            return bytes;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ignore) {

            }
        }

    }

    /**
     * 反序列化
     *
     * @param data  序列化后的byte[]值
     * @param clazz 反序列化后的对象
     * @return 返回的对象
     */

    public static <T> T deserializer(byte[] data, Class<T> clazz) {
        Kryo kryo = kryos.get();
        Registration registration = registrationMap.get(clazz);
        if (registration == null) {
            registration = kryo.register(clazz);
            registrationMap.put(clazz, registration);
        }
        T object = null;
        ByteArrayInputStream byteArrayInputStream = null;
        Input input;
        try {
            byteArrayInputStream = new ByteArrayInputStream(data);
            input = new Input(byteArrayInputStream);
            object = (T) kryo.readObject(input, registration.getType());
            input.close();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException ignore) {
            }
        }
        return object;
    }
}
