package com.orionn.optimizer.tweaks;

import com.orionn.optimizer.core.TWSParser;
import com.orionn.optimizer.shizuku.ShizukuManager;

import java.io.File;
import java.util.Map;

public class TweakEngine {

    public static void execute(File twsFile) {

        Map<String, String> data = TWSParser.parse(twsFile);

        if (data.isEmpty()) return;

        String name = data.get("name");
        String risk = data.get("risk");

        for (Map.Entry<String, String> entry : data.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            if (key.startsWith("action")) {
                runAction(value);
            }
        }
    }

    private static void runAction(String action) {

        switch (action) {

            case "close_background_apps":
                ShizukuManager.closeApp("com.facebook.katana");
                ShizukuManager.closeApp("com.instagram.android");
                break;

            case "boost_performance":
                ShizukuManager.runCommand("cmd activity idle-maintenance");
                break;

            case "reduce_animations":
                ShizukuManager.runCommand("settings put global window_animation_scale 0.5");
                ShizukuManager.runCommand("settings put global transition_animation_scale 0.5");
                ShizukuManager.runCommand("settings put global animator_duration_scale 0.5");
                break;

            case "set_refresh_rate_120":
                ShizukuManager.setRefreshRate("120");
                break;

            case "clear_cache":
                ShizukuManager.runCommand("pm trim-caches 999G");
                break;

            default:
                break;
        }
    }
}
