package Psychic.Core.AbilityConfig.Java;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static Plugin plugin = null;
    private static final Map<Class<?>, Object> abilityInstances = new HashMap<>();

    public ConfigManager(Plugin plugin) {
        ConfigManager.plugin = plugin;
    }

    public static void loadConfig(Object instance) {
        Class<?> clazz = instance.getClass();
        Name nameAnnotation = clazz.getAnnotation(Name.class);

        if (nameAnnotation == null) return;

        abilityInstances.put(clazz, instance); // 클래스와 인스턴스 매핑 저장
        reloadConfig(instance);
    }

    public static void reloadConfig(Object instance) {
        Class<?> clazz = instance.getClass();
        Name nameAnnotation = clazz.getAnnotation(Name.class);
        if (nameAnnotation == null) return;

        String abilityName = nameAnnotation.value();
        File abilitiesFolder = new File(plugin.getDataFolder(), "abilities");
        File configFile = new File(abilitiesFolder, abilityName + ".yml");

        if (!configFile.exists()) {
            saveDefaultConfig(instance);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                field.setAccessible(true);
                String path = field.getName();

                if (config.contains(path)) {
                    try {
                        Object value = config.get(path);
                        // 필드 타입에 맞게 변환
                        if (field.getType() == int.class && value instanceof Number) {
                            field.set(instance, ((Number) value).intValue());
                        } else if (field.getType() == double.class && value instanceof Number) {
                            field.set(instance, ((Number) value).doubleValue());
                        } else {
                            field.set(instance, value);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void saveDefaultConfig(Object instance) {
        Class<?> clazz = instance.getClass();
        Name nameAnnotation = clazz.getAnnotation(Name.class);

        File abilitiesFolder = new File(plugin.getDataFolder(), "abilities");
        if (!abilitiesFolder.exists()) {
            abilitiesFolder.mkdirs();
        }

        File configFile = new File(abilitiesFolder, nameAnnotation.value() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                field.setAccessible(true);
                String path = field.getName();
                try {
                    config.set(path, field.get(instance));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 모든 능력의 설정을 다시 로드하는 메서드
    public static void reloadAllConfigs() {
        for (Object instance : abilityInstances.values()) {
            reloadConfig(instance);
        }
    }
}