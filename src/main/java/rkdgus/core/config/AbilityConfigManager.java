package rkdgus.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rkdgus.ability.Ability;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class AbilityConfigManager {

    private final JavaPlugin plugin;

    public AbilityConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration load(String name) {
        File folder = new File(plugin.getDataFolder(), "abilities");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }

        return YamlConfiguration.loadConfiguration(file);
    }
    public void resetAll() {
        File folder = new File(plugin.getDataFolder(), "abilities");

        if (!folder.exists()) return;

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                file.delete();
            }
        }
    }

    public void createDefault(String name, Ability ability) {
        YamlConfiguration config = load(name);

        try {
            for (Field field : ability.getClass().getDeclaredFields()) {

                if (!field.isAnnotationPresent(config.class)) continue;

                field.setAccessible(true);

                Object def = field.get(ability);

                if (def instanceof org.bukkit.Material mat) {
                    config.set(field.getName(), mat.name());
                } else {
                    config.set(field.getName(), def);
                }
            }
        } catch (Exception ignored) {}

        save(config, name);
    }


    public void save(YamlConfiguration config, String name) {
        try {
            File file = new File(plugin.getDataFolder(), "abilities/" + name + ".yml");
            config.save(file);
        } catch (IOException ignored) {}
    }
}