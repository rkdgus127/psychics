package Psychic.Core.AbilityClass;

import Psychic.Core.Main.Psychic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Ability implements AbilityConcept, Listener {

    @Override
    public void apply(Player player) {
        Bukkit.getPluginManager().registerEvents(this, Psychic.getInstance());
        player.sendMessage("§c[능력] " + getClass().getSimpleName() + " 능력이 부여되었습니다!");
    }

    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        player.sendMessage("§7[능력] " + getClass().getSimpleName() + " 능력이 제거되었습니다.");
    }
}
