package com.orionn.optimizer.core;

import android.os.Build;

public final class TWSValidator {
    private TWSValidator() {
    }

    public static String check(TWSProfile profile) {
        if (profile == null) {
            return "Perfil vacío";
        }

        if (Build.VERSION.SDK_INT < profile.minAndroid) {
            return "Tu sistema no es compatible con este tweak aún";
        }

        if (profile.requiresShizuku) {
            return "Este tweak puede requerir Shizuku";
        }

        return "Compatible";
    }
}
