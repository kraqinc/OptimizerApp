package com.orionn.optimizer.core;

import android.content.Context;
import android.content.res.AssetManager;

import com.orionn.optimizer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class TWSAssetInstaller {
    private TWSAssetInstaller() {
    }

    public static int installBundledTweaks(Context context) {
        int count = 0;

        try {
            AssetManager assetManager = context.getAssets();
            String[] list = assetManager.list("tweaks_repo");
            if (list == null) {
                return 0;
            }

            File targetDir = TWSRepository.getTweaksDir(context);

            for (String name : list) {
                if (name == null || !name.endsWith(".tws")) {
                    continue;
                }

                File target = new File(targetDir, name);
                if (target.exists()) {
                    continue;
                }

                try (InputStream input = assetManager.open("tweaks_repo/" + name)) {
                    FileUtils.copyStream(input, target);
                    count++;
                } catch (IOException ignored) {
                } catch (Exception ignored) {
                }
            }
        } catch (IOException ignored) {
        }

        return count;
    }
}
