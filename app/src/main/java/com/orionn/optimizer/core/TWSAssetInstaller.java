package com.orionn.optimizer.core;

import java.io.InputStream;
import java.io.OutputStream;

public class TWSAssetInstaller {

    public static void install(InputStream input, OutputStream target) {
        try {
            FileUtils.copyStream(input, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
