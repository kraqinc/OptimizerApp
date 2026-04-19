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

    public static List<File> listTweaks(Context context) {
        File dir = getRepoDir(context);
        List<File> files = new ArrayList<>();
        File[] arr = dir.listFiles();
        if (arr == null) {
            return files;
        }
        for (File file : arr) {
            if (file.isFile() && file.getName().endsWith(".tws")) {
                files.add(file);
            }
        }
        return files;
    }

    public static File getTweak(Context context, String name) {
        String fileName = name.endsWith(".tws") ? name : name + ".tws";
        return new File(getRepoDir(context), fileName);
    }
}
