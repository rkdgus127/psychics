package Psychic.Core.AbilityClass;

import org.bukkit.entity.Player;

public interface AbilityConcept {
    void apply(Player player);
    void remove(Player player); // 선택사항
}