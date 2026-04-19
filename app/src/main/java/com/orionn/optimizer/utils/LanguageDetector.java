package com.orionn.optimizer.utils;

import java.util.Locale;

public final class LanguageDetector {
    private LanguageDetector() {
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static boolean isSpanish() {
        return "es".equalsIgnoreCase(getLanguage());
    }
}
