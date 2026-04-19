package com.orionn.optimizer.tweaks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class CommandRunner {
    private CommandRunner() {
    }

    public static Result run(String command) {
        Result su = runProcess(new String[]{"su", "-c", command});
        if (su.exitCode == 0) {
            return su;
        }

        Result sh = runProcess(new String[]{"sh", "-c", command});
        if (sh.exitCode == 0) {
            return sh;
        }

        return su.output.isEmpty() ? sh : su;
    }

    private static Result runProcess(String[] cmd) {
        Process process = null;
        StringBuilder builder = new StringBuilder();
        int exit = -1;

        try {
            process = Runtime.getRuntime().exec(cmd);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }

            exit = process.waitFor();
        } catch (Exception e) {
            builder.append(e.getMessage() == null ? "" : e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return new Result(exit, builder.toString().trim());
    }

    public static final class Result {
        public final int exitCode;
        public final String output;

        public Result(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output == null ? "" : output;
        }
    }
}
