package com.orionn.optimizer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static void copy(InputStream in, File outFile) throws Exception {
        ensureParent(outFile);

        try (InputStream input = new BufferedInputStream(in);
             FileOutputStream out = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[8192];
            int len;

            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            out.flush();
        }
    }

    public static void copyStream(InputStream in, File outFile) throws Exception {
        copy(in, outFile);
    }

    public static String readText(File file) throws Exception {
        if (file == null || !file.exists()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }

        return builder.toString().trim();
    }

    public static String readText(InputStream in) throws Exception {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }

        return builder.toString().trim();
    }
}
