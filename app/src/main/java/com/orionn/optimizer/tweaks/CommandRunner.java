package com.orionn.optimizer.tweaks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class CommandRunner {
    private CommandRunner() {
    }

    public static int run(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            process.waitFor();
            return process.exitValue();
        } catch (Exception ignored) {
            return -1;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static String runAndCapture(String command) {
        Process process = null;
        StringBuilder builder = new StringBuilder();

        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }
            process.waitFor();
            return builder.toString().trim();
        } catch (Exception ignored) {
            return "";
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
