package com.orionn.optimizer.shizuku;

public class ShizukuManager {

    public static void runCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (Exception e) {
        }
    }

    public static void closeApp(String pkg) {
        runCommand("am force-stop " + pkg);
    }

    public static void clearCache(String pkg) {
        runCommand("pm clear " + pkg);
    }

    public static void setRefreshRate(String value) {
        runCommand("settings put system peak_refresh_rate " + value);
        runCommand("settings put system min_refresh_rate " + value);
    }
}
