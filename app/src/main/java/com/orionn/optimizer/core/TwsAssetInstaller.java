package com.orionn.optimizer.core;

import android.content.Context;

import com.orionn.optimizer.utils.FileUtils;

import java.io.File;
import java.io.InputStream;

public final class TwsAssetInstaller {
    private TwsAssetInstaller() {
    }

    public static int install(Context context) {
        int count = 0;
        try {
            String[] files = context.getAssets().list("tweaks_repo");
            if (files == null) {
                return 0;
            }

            File targetDir = TwsRepository.getRepoDir(context);

            for (String fileName : files) {
                if (fileName == null || !fileName.endsWith(".tws")) {
                    continue;
                }

                File target = new File(targetDir, fileName);
                if (target.exists()) {
                    continue;
                }

                try (InputStream in = context.getAssets().open("tweaks_repo/" + fileName)) {
                    FileUtils.copy(in, target);
                    count++;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return count;
    }
}
