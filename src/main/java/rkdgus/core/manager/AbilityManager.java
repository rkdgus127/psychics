package rkdgus.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import rkdgus.ability.Ability;
import rkdgus.core.psychics;

import java.util.*;

public class AbilityManager {

    private final Map<UUID, Set<Ability>> data = new HashMap<>();

    public void add(Player player, Ability ability) {
        ability.setOwner(player.getUniqueId());

        data.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(ability);

        Bukkit.getPluginManager().registerEvents(ability, psychics.getInstance());
        ability.onAttach(player);
    }

    public boolean remove(Player player, String name) {
        Set<Ability> set = data.get(player.getUniqueId());
        if (set == null) return false;

        for (Ability ability : new HashSet<>(set)) {
            if (ability.getName().equalsIgnoreCase(name)) {
                ability.onDetach(player);
                HandlerList.unregisterAll(ability);
                set.remove(ability);
                return true;
            }
        }

        return false;
    }

    public void reloadAll() {

        for (UUID uuid : new HashSet<>(data.keySet())) {

            Set<Ability> abilities = data.get(uuid);
            if (abilities == null) continue;

            for (Ability ability : new HashSet<>(abilities)) {

                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                ability.onDetach(player);
                HandlerList.unregisterAll(ability);

                // 🔥 새로 생성
                Ability newAbility = psychics.getInstance()
                        .getAbilityRegistry()
                        .create(ability.getName());

                if (newAbility == null) continue;

                add(player, newAbility);
            }
        }
    }

    public Set<Ability> get(Player player) {
        return data.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }
}