package Psychic.Core.AbilityConfig.Java;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ConfigProcessor {

    private final JavaPlugin plugin;

    public ConfigProcessor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void process(Class<?> clazz) {
        try {
            String fileName = null;
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Config.class)) continue;
                if (!Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);

                // 파일 이름 설정
                if (field.getName().equalsIgnoreCase("Name") && field.getType() == String.class) {
                    fileName = (String) field.get(null);
                    break;
                }
            }

            if (fileName == null) {
                plugin.getLogger().warning(clazz.getSimpleName() + "에 @Config Name 필드가 없음!");
                return;
            }

            // config 파일
            File file = new File(plugin.getDataFolder(), fileName + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            // 필드들을 config에 저장/로딩
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Config.class)) continue;
                if (!Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                String key = field.getName();
                Object defaultValue = field.get(null);

                if (!config.contains(key)) {
                    config.set(key, defaultValue);
                } else {
                    Object value = config.get(key);
                    field.set(null, value);
                }
            }

            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Config 처리 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
