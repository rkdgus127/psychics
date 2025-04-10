package Psychic.Core.Manager;

import Psychic.Core.AbilityClass.Abstract.Ability;
import Psychic.Core.AbilityClass.InterFace.AbilityConcept;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AbilityManager {
    private static final Map<UUID, Set<AbilityConcept>> abilityMap = new HashMap<>();

    public static void addAbility(UUID uuid, AbilityConcept ability) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        Set<AbilityConcept> abilities = abilityMap.computeIfAbsent(uuid, k -> new HashSet<>());

        // 중복 능력 부여 방지
        if (abilities.stream().anyMatch(a -> a.getClass() == ability.getClass())) {
            player.sendMessage("§e[능력] 이미 이 능력을 가지고 있습니다.");
            return;
        }

        abilities.add(ability);
        ability.apply(player);
    }

    public static Set<AbilityConcept> getAbilities(UUID uuid) {
        return abilityMap.getOrDefault(uuid, Collections.emptySet());
    }

    public static void clearAllAbilities(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Set<AbilityConcept> abilities = abilityMap.remove(uuid);
        if (abilities == null || player == null) return;

        for (AbilityConcept ability : abilities) {
            ability.remove(player);
        }
    }
    public static boolean hasAbility(UUID uuid, Class<? extends AbilityConcept> abilityClass) {
        Set<AbilityConcept> abilities = abilityMap.get(uuid);
        if (abilities == null) return false;

        return abilities.stream().anyMatch(a -> a.getClass() == abilityClass);
    }

    public static boolean hasAbility(Player player, Class<? extends AbilityConcept> abilityClass) {
        return hasAbility(player.getUniqueId(), abilityClass);
    }

    public static boolean hasAnyAbility(Player player) {
        Set<AbilityConcept> abilities = abilityMap.get(player.getUniqueId());
        return abilities != null && !abilities.isEmpty();
    }

}
