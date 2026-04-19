package com.orionn.optimizer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

public final class TwsParser {
    private TwsParser() {
    }

    public static TweakItem parse(File file) {
        TweakItem item = new TweakItem();

        if (file == null || !file.exists()) {
            return item;
        }

        item.sourceFileName = file.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }

                int idx = line.indexOf('=');
                if (idx <= 0) {
                    continue;
                }

                String key = line.substring(0, idx).trim().toLowerCase(Locale.US);
                String value = line.substring(idx + 1).trim();

                switch (key) {
                    case "name":
                        item.name = value;
                        break;
                    case "description":
                        item.description = value;
                        break;
                    case "risk":
                        item.risk = value;
                        break;
                    case "warning":
                        item.warning = value;
                        break;
                    case "min_android":
                    case "minandroid":
                        try {
                            item.minAndroid = Integer.parseInt(value);
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                    case "requires_shizuku":
                    case "requiresshizuku":
                        item.requiresShizuku = "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
                        break;
                    case "on_enable":
                        parseList(value, item.onEnable);
                        break;
                    case "on_disable":
                        parseList(value, item.onDisable);
                        break;
                    case "tags":
                        parseList(value, item.tags);
                        break;
                    default:
                        if (key.startsWith("action")) {
                            parseList(value, item.onEnable);
                        }
                        break;
                }
            }
        } catch (Exception ignored) {
        }

        if (item.name.isEmpty()) {
            String fileName = file.getName();
            item.name = fileName.endsWith(".tws") ? fileName.substring(0, fileName.length() - 4) : fileName;
        }

        return item;
    }

    private static void parseList(String raw, java.util.List<String> out) {
        if (raw == null || raw.trim().isEmpty()) {
            return;
        }

        String[] parts = raw.split("\\|");
        for (String part : parts) {
            String action = part.trim();
            if (!action.isEmpty()) {
                out.add(action);
            }
        }
    }
}
