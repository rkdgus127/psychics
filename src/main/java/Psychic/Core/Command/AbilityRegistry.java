package Psychic.Core.Command;

import Psychic.Core.Abstract.Ability;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityRegistry {
    private static final Map<String, Class<? extends Ability>> ABILITY_CLASSES = new HashMap<>();
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) return;

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader())
                        .setScanners(new SubTypesScanner(false))
                // 특정 패키지만 스캔하도록 설정할 수도 있습니다
                // .forPackages("your.base.package")
        );

        reflections.getSubTypesOf(Ability.class).stream()
                .filter(clazz -> !clazz.getName().contains("$"))
                .forEach(clazz -> ABILITY_CLASSES.put(clazz.getSimpleName(), clazz));

        isInitialized = true;
    }

    public static List<String> getAbilityNames() {
        return new ArrayList<>(ABILITY_CLASSES.keySet());
    }

    public static Class<? extends Ability> getAbilityClass(String name) {
        return ABILITY_CLASSES.get(name);
    }
}