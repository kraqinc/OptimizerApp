package com.orionn.optimizer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FileUtils {
    private FileUtils() {
    }

    public static void copy(InputStream inputStream, File target) throws Exception {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (InputStream in = inputStream; FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }
    }
}
