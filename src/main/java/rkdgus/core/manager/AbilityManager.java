package rkdgus.core.manager;

import org.bukkit.event.HandlerList;
import rkdgus.ability.Ability;
import rkdgus.core.psychics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AbilityManager {

    private final Map<UUID, Set<Ability>> data = new HashMap<>();

    public void add(Player player, Ability ability) {
        data.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(ability);

        Bukkit.getPluginManager().registerEvents(ability, psychics.getInstance());
        ability.onAttach(player);
    }

    public void remove(Player player, String name) {
        Set<Ability> set = data.get(player.getUniqueId());
        if (set == null) return;

        set.removeIf(ability -> {
            if (ability.getName().equalsIgnoreCase(name)) {
                ability.onDetach(player);
                HandlerList.unregisterAll(ability);
                return true;
            }
            return false;
        });
    }

    public Set<Ability> get(Player player) {
        return data.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }
}