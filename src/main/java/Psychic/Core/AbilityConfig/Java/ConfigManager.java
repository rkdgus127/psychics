package Psychic.Core.AbilityConfig.Java;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;

public class ConfigManager {
    private static Plugin plugin = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static void loadConfig(Object instance) {
        Class<?> clazz = instance.getClass();
        Name nameAnnotation = clazz.getAnnotation(Name.class);

        if (nameAnnotation == null) {
            return;
        }

        String abilityName = nameAnnotation.value();
        File configFile = new File(plugin.getDataFolder(), abilityName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        boolean needsSave = false;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                field.setAccessible(true);
                String path = field.getName();

                try {
                    // 설정이 없으면 기본값 저장
                    if (!config.contains(path)) {
                        config.set(path, field.get(instance));
                        needsSave = true;
                    } else {
                        // 설정이 있으면 로드
                        Object value = config.get(path);
                        field.set(instance, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (needsSave) {
            try {
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}