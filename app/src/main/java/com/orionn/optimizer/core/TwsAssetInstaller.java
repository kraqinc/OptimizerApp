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
            String[] list = context.getAssets().list("tweaks_repo");
            if (list == null) {
                return 0;
            }

            File targetDir = TwsRepository.getRepoDir(context);

            for (String name : list) {
                if (name == null || !name.endsWith(".tws")) {
                    continue;
                }

                File target = new File(targetDir, name);
                if (target.exists()) {
                    continue;
                }

                try (InputStream in = context.getAssets().open("tweaks_repo/" + name)) {
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
