package com.orionn.optimizer.core;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class TwsRepository {
    private TwsRepository() {
    }

    public static File getRepoDir(Context context) {
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
        String fileName = name == null ? "unknown.tws" : name.trim();
        if (!fileName.endsWith(".tws")) {
            fileName = fileName + ".tws";
        }
        return new File(getRepoDir(context), fileName);
    }

    public static List<File> listTweaks(Context context) {
        List<File> result = new ArrayList<>();
        File dir = getRepoDir(context);
        File[] files = dir.listFiles();

        if (files == null) {
            return result;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".tws")) {
                result.add(file);
            }
        }

        return result;
    }
}
