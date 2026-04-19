package com.orionn.optimizer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public final class TWSParser {
    private TWSParser() {
    }

    public static TWSProfile parse(File file) {
        TWSProfile profile = new TWSProfile();

        if (file == null || !file.exists()) {
            return profile;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }

                int index = line.indexOf('=');
                if (index <= 0) {
                    continue;
                }

                String key = line.substring(0, index).trim().toLowerCase(Locale.US);
                String value = line.substring(index + 1).trim();

                if ("name".equals(key)) {
                    profile.name = value;
                } else if ("description".equals(key)) {
                    profile.description = value;
                } else if ("risk".equals(key)) {
                    profile.risk = value;
                } else if ("warning".equals(key)) {
                    profile.warning = value;
                } else if ("requires_shizuku".equals(key) || "requiresshizuku".equals(key)) {
                    profile.requiresShizuku = "true".equalsIgnoreCase(value) || "1".equals(value);
                } else if ("min_android".equals(key) || "minandroid".equals(key)) {
                    try {
                        profile.minAndroid = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                } else if ("action".equals(key) || key.startsWith("action")) {
                    profile.actions.add(value);
                }
            }
        } catch (IOException ignored) {
        }

        if (profile.name.isEmpty()) {
            String fileName = file.getName();
            if (fileName.endsWith(".tws")) {
                profile.name = fileName.substring(0, fileName.length() - 4);
            } else {
                profile.name = fileName;
            }
        }

        return profile;
    }
}
