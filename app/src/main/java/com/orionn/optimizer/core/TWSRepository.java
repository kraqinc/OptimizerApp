package com.orionn.optimizer.core;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class TWSRepository {
    private TWSRepository() {
    }

    public static File getTweaksDir(Context context) {
        File base = context.getExternalFilesDir(null);
        if (base == null) {
            base = context.getFilesDir();
        }

        File dir = new File(base, "tweaks_repo");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getTweakFile(Context context, String name) {
        String safe = name == null ? "unknown" : name.trim();
        if (!safe.endsWith(".tws")) {
            safe = safe + ".tws";
        }
        return new File(getTweaksDir(context), safe);
    }

    public static List<File> listTweaks(Context context) {
        File dir = getTweaksDir(context);
        List<File> files = new ArrayList<>();
        File[] list = dir.listFiles();
        if (list == null) {
            return files;
        }
        for (File file : list) {
            if (file.isFile() && file.getName().endsWith(".tws")) {
                files.add(file);
            }
        }
        return files;
    }
}
