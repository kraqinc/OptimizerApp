package com.orionn.optimizer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static void ensureParent(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static void copyStream(InputStream in, File outFile) throws Exception {
        ensureParent(outFile);
        FileOutputStream out = new FileOutputStream(outFile);

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        out.close();
        in.close();
    }
}
