package com.orionn.optimizer.tweaks;

import android.os.Build;

import com.orionn.optimizer.core.TweakItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ActionOnJava {
    private ActionOnJava() {
    }

    public static String applyTweak(TweakItem item, boolean enabled) {
        if (item == null) {
            return "Tweak vacío";
        }

        if (Build.VERSION.SDK_INT < item.minAndroid) {
            return "Tu sistema no es compatible con este tweak aún";
        }

        List<String> actions = enabled ? item.onEnable : item.onDisable;
        if (actions == null || actions.isEmpty()) {
            return enabled ? "No hay acciones al activar" : "No hay acciones al desactivar";
        }

        List<String> results = new ArrayList<>();
        for (String action : actions) {
            String result = executeAction(action);
            if (!result.isEmpty()) {
                results.add(result);
            }
        }

        if (results.isEmpty()) {
            return "Aplicado";
        }

        StringBuilder builder = new StringBuilder();
        for (String line : results) {
            builder.append(line).append('\n');
        }
        return builder.toString().trim();
    }

    private static String executeAction(String action) {
        if (action == null) {
            return "";
        }

        String clean = action.trim().toLowerCase(Locale.US);
        String command;

        switch (clean) {
            case "close_background_apps":
                command = "am force-stop com.facebook.katana; am force-stop com.instagram.android; am force-stop com.tiktok.android";
                break;
            case "reduce_animations":
                command = "settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; settings put global animator_duration_scale 0.5";
                break;
            case "boost_performance":
                command = "cmd activity idle-maintenance";
                break;
            case "clear_cache":
                command = "pm trim-caches 1G";
                break;
            case "set_refresh_rate_120":
                command = "settings put system peak_refresh_rate 120; settings put system min_refresh_rate 120";
                break;
            case "battery_saver":
                command = "settings put global low_power 1";
                break;
            case "restore_default":
                command = "settings put global window_animation_scale 1; settings put global transition_animation_scale 1; settings put global animator_duration_scale 1";
                break;
            default:
                command = action;
                break;
        }

        String output = runCommand(command);
        if (output.isEmpty()) {
            return clean + " OK";
        }

        return clean + " " + output;
    }

    private static String runCommand(String command) {
        String shellResult = runWithShell(command);
        if (!shellResult.isEmpty()) {
            return shellResult;
        }

        String rootResult = runWithSu(command);
        if (!rootResult.isEmpty()) {
            return rootResult;
        }

        return "";
    }

    private static String runWithShell(String command) {
        return runProcess(new String[]{"sh", "-c", command});
    }

    private static String runWithSu(String command) {
        return runProcess(new String[]{"su", "-c", command});
    }

    private static String runProcess(String[] cmd) {
        Process process = null;
        StringBuilder builder = new StringBuilder();

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
            process.waitFor();
        } catch (Exception ignored) {
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return builder.toString().trim();
    }
}
