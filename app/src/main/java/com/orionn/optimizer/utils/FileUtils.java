package com.orionn.optimizer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {
    private FileUtils() {
    }

    public static void ensureParent(File file) {
        if (file == null) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static void copyStream(InputStream input, File target) throws IOException {
        ensureParent(target);
        try (InputStream in = input; FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }
    }

    public static String safeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "unknown";
        }
        return name.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
