package rkdgus.ability;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rkdgus.core.config.config;
import rkdgus.core.psychics;
import rkdgus.core.util.PsyDamage;

import java.lang.reflect.Field;
import java.util.UUID;

public abstract class psy implements Ability {

    private UUID owner;
    private YamlConfiguration config;

    @Override
    public void setOwner(UUID uuid) {
        this.owner = uuid;

        this.config = psychics.getInstance()
                .getConfigManager()
                .load(getName());

        loadconfigs();

        psychics.getInstance()
                .getConfigManager()
                .save(config, getName());
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    protected boolean isOwner(Player player) {
        return player.getUniqueId().equals(owner);
    }

    protected float pdam(double base) {
        Player player = org.bukkit.Bukkit.getPlayer(getOwner());
        if (player == null) return (float) base;

        return (float) PsyDamage.psydamage(player, base);
    }

    private void loadconfigs() {
        for (Field field : this.getClass().getDeclaredFields()) {

            if (!field.isAnnotationPresent(config.class)) continue;

            field.setAccessible(true);

            config annotation = field.getAnnotation(config.class);
            String path = annotation.path().isEmpty() ? field.getName() : annotation.path();

            try {
                Object value;

                if (!config.contains(path)) {
                    Object def = field.get(this);

                    // 🔥 Material은 문자열로 저장
                    if (def instanceof org.bukkit.Material mat) {
                        config.set(path, mat.name());
                    } else {
                        config.set(path, def);
                    }

                    value = def;

                } else {
                    value = config.get(path);
                }

                // 🔥 타입 변환
                if (field.getType() == org.bukkit.Material.class) {
                    if (value instanceof String str) {
                        field.set(this, org.bukkit.Material.valueOf(str));
                    }
                } else if (field.getType() == long.class && value instanceof Number) {
                    field.set(this, ((Number) value).longValue());
                } else if (field.getType() == int.class && value instanceof Number) {
                    field.set(this, ((Number) value).intValue());
                } else if (field.getType() == double.class && value instanceof Number) {
                    field.set(this, ((Number) value).doubleValue());
                } else {
                    field.set(this, value);
                }

            } catch (Exception ignored) {
            }
        }
    }
}