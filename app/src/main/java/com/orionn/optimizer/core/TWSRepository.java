package com.orionn.optimizer.core;

import java.io.File;

public class TWSRepository {

    public static File getLocalFolder() {
        return new File("tweaks_repo");
    }

    public static File getTweak(String name) {
        return new File("tweaks_repo/" + name + ".tws");
    }
}
