package com.coke.wolf.common.utils;

import java.io.File;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/18 8:11 上午
 */
public class FileUtils {

    public static boolean ensureDirOK(final String dirName) {
        boolean ok = false;
        if (dirName != null) {
            File f = new File(dirName);
            if (!f.exists()) {
                ok = f.mkdirs();
            }
        }
        return ok;
    }

    public static boolean isEmpty(File[] files) {
        return files != null && files.length > 0;
    }

    public static boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }
}
