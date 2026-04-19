package com.orionn.optimizer.core;

import java.util.ArrayList;
import java.util.List;

public class TWSProfile {
    public String name = "";
    public String description = "";
    public String risk = "low";
    public String warning = "";
    public boolean requiresShizuku = false;
    public int minAndroid = 21;
    public final List<String> actions = new ArrayList<>();
}
