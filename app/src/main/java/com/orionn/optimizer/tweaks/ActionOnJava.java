package com.orionn.optimizer.tweaks;

import android.os.Build;
import android.content.pm.PackageManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import rikka.shizuku.Shizuku;

public final class ActionOnJava {

    private ActionOnJava() {
    }

    public static boolean isShizukuReady() {
        if (Shizuku.isPreV11()) {
            return false;
        }
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requestShizukuPermission(int requestCode) {
        if (Shizuku.isPreV11()) {
            return false;
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        Shizuku.requestPermission(requestCode);
        return false;
    }

    public static String applyTweak(TweakItem item) {
        if (item == null) {
            return "Tweak vacío";
        }

        StringBuilder report = new StringBuilder();

        if (Build.VERSION.SDK_INT < item.minAndroid) {
            return "Tu sistema no es compatible con este tweak aún";
        }

        List<String> actions = item.enabled ? item.onEnable : item.onDisable;
        if (actions == null || actions.isEmpty()) {
            return item.enabled ? "No hay acciones al activar" : "No hay acciones al desactivar";
        }

        for (String action : actions) {
            String result = runAction(action);
            if (!result.isEmpty()) {
                report.append(result).append('\n');
            }
        }

        String out = report.toString().trim();
        return out.isEmpty() ? "Aplicado" : out;
    }

    private static String runAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return "";
        }

        String cmd = null;

        switch (action.trim()) {
            case "close_background_apps":
                cmd = "am force-stop com.facebook.katana; am force-stop com.instagram.android";
                break;
            case "reduce_animations":
                cmd = "settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; settings put global animator_duration_scale 0.5";
                break;
            case "boost_performance":
                cmd = "cmd activity idle-maintenance";
                break;
            case "clear_cache":
                cmd = "pm trim-caches 1G";
                break;
            case "set_refresh_rate_120":
                cmd = "settings put system peak_refresh_rate 120; settings put system min_refresh_rate 120";
                break;
            default:
                cmd = action;
                break;
        }

        if (isShizukuReady()) {
            String out = runWithShizuku(cmd);
            if (!out.isEmpty()) {
                return out;
            }
        }

        int exit = runWithShell(cmd);
        return exit == 0 ? action + " OK" : action + " FAIL";
    }

    private static int runWithShell(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            return p.waitFor();
        } catch (Exception e) {
            return -1;
        }
    }

    private static String runWithShizuku(String cmd) {
        try {
            Class<?> cls = Class.forName("rikka.shizuku.Shizuku");
            Method method = cls.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
            Object remote = method.invoke(null, new String[]{"sh", "-c", cmd}, null, null);

            Method input = remote.getClass().getMethod("getInputStream");
            Method error = remote.getClass().getMethod("getErrorStream");
            Method destroy = remote.getClass().getMethod("destroy");
            Method waitFor = remote.getClass().getMethod("waitFor");

            java.io.InputStream in = (java.io.InputStream) input.invoke(remote);
            java.io.InputStream err = (java.io.InputStream) error.invoke(remote);

            StringBuilder builder = new StringBuilder();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(err))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }

            waitFor.invoke(remote);
            destroy.invoke(remote);

            return builder.toString().trim();
        } catch (Throwable t) {
            return "";
        }
    }
}
