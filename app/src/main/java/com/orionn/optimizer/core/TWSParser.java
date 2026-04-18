package com.orionn.optimizer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class TWSParser {

    public static Map<String, String> parse(File file) {

        Map<String, String> data = new HashMap<>();

        if (file == null || !file.exists()) {
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.contains("=")) {

                    String[] parts = line.split("=", 2);

                    if (parts.length == 2) {
                        data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }

        } catch (Exception e) {
        }

        return data;
    }
}
