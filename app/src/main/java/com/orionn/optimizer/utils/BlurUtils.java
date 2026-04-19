package com.orionn.optimizer.utils;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;

public final class BlurUtils {
    private BlurUtils() {
    }

    public static void applySoftBlur(View view) {
        if (view == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.CLAMP));
        } else {
            view.setAlpha(0.92f);
        }
    }
}
