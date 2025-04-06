package Psychic.Core.Manager;

import Psychic.Core.AbilityClass.AbilityConcept;

import java.util.HashMap;
import java.util.UUID;

public class AbilityManager {
    private static final HashMap<UUID, AbilityConcept> abilityMap = new HashMap<>();

    public static void setAbility(UUID uuid, String name, AbilityConcept abilityConcept) {
        abilityMap.put(uuid, abilityConcept);
    }

    public static AbilityConcept getAbility(UUID uuid) {
        return abilityMap.get(uuid);
    }

    public static void removeAbility(UUID uuid) {
        abilityMap.remove(uuid);
    }
}