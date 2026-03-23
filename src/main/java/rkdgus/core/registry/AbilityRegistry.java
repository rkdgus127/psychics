package rkdgus.core.registry;

import rkdgus.ability.Ability;
import rkdgus.core.util.ReflectionUtil;

import java.util.*;

public class AbilityRegistry {

    private final Map<String, Class<? extends Ability>> map = new HashMap<>();

    public void registerAll(String pkg) {
        for (Class<?> clazz : ReflectionUtil.getClasses(pkg)) {

            if (!Ability.class.isAssignableFrom(clazz)) continue;
            if (clazz.isInterface()) continue;

            register((Class<? extends Ability>) clazz);
        }
    }

    public void register(Class<? extends Ability> clazz) {
        try {
            Ability ability = clazz.getDeclaredConstructor().newInstance();
            map.put(ability.getName().toLowerCase(), clazz);
        } catch (Exception ignored) {}
    }

    public Ability create(String name) {
        Class<? extends Ability> clazz = map.get(name.toLowerCase());
        if (clazz == null) return null;

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getNames() {
        return new ArrayList<>(map.keySet());
    }
}