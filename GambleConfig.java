package com.gambleutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GambleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("gambleutils.json");
    public String theme = "Midnight";
    public int accent = 0xFF5865F2;
    public int panel = 0xFF232428;
    public int text = 0xFFF2F3F5;
    public int bar = 0xFF5865F2;
    public String fontMode = "Default";
    public boolean showBar = true;
    public String itemName = "";
    public int amount = 1;
    public String manualUnitValue = "";
    public int durationSeconds = 30;

    public static GambleConfig load() {
        try {
            if (Files.exists(PATH)) {
                GambleConfig data = GSON.fromJson(Files.readString(PATH), GambleConfig.class);
                if (data != null) return data;
            }
        } catch (Exception ex) { System.err.println("GambleUtils: could not load config: " + ex); }
        return new GambleConfig();
    }
    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException ex) { System.err.println("GambleUtils: could not save config: " + ex); }
    }
    public void applyTheme(String name) {
        theme = name;
        switch (name) {
            case "Light" -> { accent = 0xFF5865F2; panel = 0xFFE9ECF3; text = 0xFF17202A; bar = 0xFF5865F2; }
            case "Emerald" -> { accent = 0xFF32C48D; panel = 0xFF142824; text = 0xFFE5FFF5; bar = 0xFF32C48D; }
            case "Rose" -> { accent = 0xFFEF72AE; panel = 0xFF30202B; text = 0xFFFFEAF3; bar = 0xFFEF72AE; }
            default -> { accent = 0xFF5865F2; panel = 0xFF232428; text = 0xFFF2F3F5; bar = 0xFF5865F2; }
        }
    }
}
