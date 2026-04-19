package com.orionn.optimizer.tweaks;

import com.orionn.optimizer.core.TWSProfile;
import com.orionn.optimizer.core.TWSValidator;

public final class TweakEngine {
    private TweakEngine() {
    }

    public static String apply(TWSProfile profile) {
        if (profile == null) {
            return "Perfil vacío";
        }

        String compatibility = TWSValidator.check(profile);
        if (!"Compatible".equals(compatibility) && compatibility.contains("no es compatible")) {
            return compatibility;
        }

        if (profile.actions.isEmpty()) {
            return "No hay acciones";
        }

        StringBuilder report = new StringBuilder();

        for (String action : profile.actions) {
            String result = executeAction(action);
            if (result != null && !result.isEmpty()) {
                report.append(result).append('\n');
            }
        }

        String output = report.toString().trim();
        if (output.isEmpty()) {
            return "Tweak aplicado";
        }
        return output;
    }

    private static String executeAction(String action) {
        if ("close_background_apps".equalsIgnoreCase(action)) {
            CommandRunner.run("am force-stop com.facebook.katana");
            CommandRunner.run("am force-stop com.instagram.android");
            return "Apps en segundo plano cerradas";
        }

        if ("reduce_animations".equalsIgnoreCase(action)) {
            CommandRunner.run("settings put global window_animation_scale 0.5");
            CommandRunner.run("settings put global transition_animation_scale 0.5");
            CommandRunner.run("settings put global animator_duration_scale 0.5");
            return "Animaciones reducidas";
        }

        if ("set_refresh_rate_120".equalsIgnoreCase(action)) {
            CommandRunner.run("settings put system peak_refresh_rate 120");
            CommandRunner.run("settings put system min_refresh_rate 120");
            return "Refresh rate solicitado";
        }

        if ("clear_cache".equalsIgnoreCase(action)) {
            CommandRunner.run("pm trim-caches 1G");
            return "Cache solicitada para limpieza";
        }

        if ("boost_performance".equalsIgnoreCase(action)) {
            CommandRunner.run("cmd activity idle-maintenance");
            return "Modo rendimiento solicitado";
        }

        return "Acción desconocida: " + action;
    }
}
