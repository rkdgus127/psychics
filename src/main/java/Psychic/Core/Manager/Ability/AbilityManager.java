package Psychic.Core.Manager.Ability;

import Psychic.Core.AbilityConcept;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Manager.Mana.Mana;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AbilityManager {
    public static final Map<UUID, Set<AbilityConcept>> abilityMap = new HashMap<>();
    public static final Map<String, Class<? extends AbilityConcept>> abilityNameMap = new HashMap<>();

    private static String getAbilityName(Class<? extends AbilityConcept> abilityClass) {
        Name nameAnnotation = abilityClass.getAnnotation(Name.class);
        return nameAnnotation != null ? nameAnnotation.value() : abilityClass.getSimpleName();
    }


    public static void addAbility(UUID uuid, AbilityConcept ability) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        Set<AbilityConcept> abilities = abilityMap.computeIfAbsent(uuid, k -> new HashSet<>());

        // 중복 능력 부여 방지
        if (abilities.stream().anyMatch(a -> a.getClass() == ability.getClass())) {
            player.sendMessage("§eIt already has this ability.");
            return;
        }

        abilities.add(ability);
        String abilityName = getAbilityName(ability.getClass());
        abilityNameMap.put(abilityName, ability.getClass());
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
            Mana.setManaRegen(player, true);
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

}
