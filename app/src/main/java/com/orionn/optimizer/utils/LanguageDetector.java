package com.orionn.optimizer.utils;

import java.util.Locale;

public class LanguageDetector {

    public static String getLanguage() {

        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static boolean isSpanish() {
        return getLanguage().equals("es");
    }

    public static boolean isEnglish() {
        return getLanguage().equals("en");
    }
}
