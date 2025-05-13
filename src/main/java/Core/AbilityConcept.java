package Core;

import org.bukkit.entity.Player;

public interface AbilityConcept {
    void apply(Player player);
    void remove(Player player); // 반드시 구현해야 함
}
