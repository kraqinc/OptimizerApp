package com.orionn.optimizer.tweaks;

import android.os.Build;

import com.orionn.optimizer.core.TweakItem;

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

        List<String> reports = new ArrayList<>();
        for (String rawAction : actions) {
            String result = executeAction(item, rawAction, enabled);
            if (result != null && !result.isEmpty()) {
                reports.add(result);
            }
        }

        if (reports.isEmpty()) {
            return "Aplicado";
        }

        StringBuilder out = new StringBuilder();
        for (String line : reports) {
            out.append(line).append('\n');
        }
        return out.toString().trim();
    }

    private static String executeAction(TweakItem item, String action, boolean enabled) {
        if (action == null) {
            return "";
        }

        String clean = action.trim();
        if (clean.isEmpty()) {
            return "";
        }

        String lower = clean.toLowerCase(Locale.US);
        String command = null;

        if (lower.startsWith("shell:")) {
            command = clean.substring(6).trim();
        } else if (lower.startsWith("su:")) {
            command = clean.substring(3).trim();
        } else if (lower.startsWith("force_stop:")) {
            command = "am force-stop " + clean.substring(11).trim();
        } else if (lower.startsWith("clear_pkg_cache:")) {
            command = "pm clear " + clean.substring(16).trim();
        } else if (lower.startsWith("set_anim:")) {
            String value = clean.substring(9).trim();
            command = "settings put global window_animation_scale " + value +
                    "; settings put global transition_animation_scale " + value +
                    "; settings put global animator_duration_scale " + value;
        } else if (lower.startsWith("set_refresh_rate:")) {
            String value = clean.substring(17).trim();
            command = "settings put system peak_refresh_rate " + value +
                    "; settings put system min_refresh_rate " + value;
        } else if (lower.startsWith("low_power:")) {
            String value = clean.substring(10).trim();
            command = "settings put global low_power " + value;
        } else {
            switch (lower) {
                case "close_background_apps":
                    command = buildClosePackagesCommand(item.packages);
                    break;
                case "kill_background_apps":
                    command = "am kill-all";
                    break;
                case "reduce_animations":
                case "disable_animations":
                    command = "settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; settings put global animator_duration_scale 0.5";
                    break;
                case "restore_default":
                case "enable_animations":
                    command = "settings put global window_animation_scale 1; settings put global transition_animation_scale 1; settings put global animator_duration_scale 1; settings put global low_power 0";
                    break;
                case "boost_performance":
                    command = "cmd activity idle-maintenance";
                    break;
                case "clear_cache":
                    command = "pm trim-caches 1G";
                    break;
                case "battery_saver":
                    command = "settings put global low_power 1";
                    break;
                case "battery_normal":
                    command = "settings put global low_power 0";
                    break;
                case "set_refresh_rate_120":
                    command = "settings put system peak_refresh_rate 120; settings put system min_refresh_rate 120";
                    break;
                default:
                    if (clean.contains(" ")) {
                        command = clean;
                    }
                    break;
            }
        }

        if (command == null || command.trim().isEmpty()) {
            return "Acción desconocida: " + clean;
        }

        CommandRunner.Result result = CommandRunner.run(command);
        if (result.exitCode == 0) {
            return clean + " OK";
        }

        if (result.output.isEmpty()) {
            return clean + " FAIL";
        }

        return clean + " FAIL " + result.output;
    }

    private static String buildClosePackagesCommand(List<String> packages) {
        StringBuilder command = new StringBuilder();

        if (packages != null && !packages.isEmpty()) {
            for (String pkg : packages) {
                String clean = pkg == null ? "" : pkg.trim();
                if (clean.isEmpty()) {
                    continue;
                }
                if (command.length() > 0) {
                    command.append("; ");
                }
                command.append("am force-stop ").append(clean);
            }
        }

        if (command.length() == 0) {
            command.append("am force-stop com.facebook.katana; ")
                   .append("am force-stop com.instagram.android; ")
                   .append("am force-stop com.tiktok.android; ")
                   .append("am force-stop com.snapchat.android");
        }

        return command.toString();
    }
}
