package Psychic.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager {

    private static final Map<UUID, String> abilityMap = new HashMap<>();

    public static void setAbility(UUID playerId, String ability) {
        abilityMap.put(playerId, ability);
    }

    public static void removeAbility(UUID playerId) {
        abilityMap.remove(playerId);
    }

    public static String getAbility(UUID playerId) {
        return abilityMap.get(playerId);
    }

    public static void clearAll() {
        abilityMap.clear();
    }
}