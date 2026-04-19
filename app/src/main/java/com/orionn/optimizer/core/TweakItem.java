package com.orionn.optimizer.core;

import java.util.ArrayList;
import java.util.List;

public class TweakItem {
    public String name = "";
    public String description = "";
    public String risk = "low";
    public String warning = "";
    public int minAndroid = 24;
    public boolean requiresShizuku = false;
    public boolean enabled = false;
    public String sourceFileName = "";
    public final List<String> onEnable = new ArrayList<>();
    public final List<String> onDisable = new ArrayList<>();
    public final List<String> tags = new ArrayList<>();
}
