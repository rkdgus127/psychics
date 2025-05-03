package Psychic.Core.AbilityClass.Abstract;

import Psychic.Core.AbilityClass.InterFace.AbilityConcept;
import Psychic.Core.Main.Psychic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Ability implements AbilityConcept, Listener {

    @Override
    public void apply(Player player) {
        Bukkit.getPluginManager().registerEvents(this, Psychic.getInstance());
        String abilityName = getClass().getSimpleName();
        String playerName = player.getName();

        // 전체에게 브로드캐스트
        Bukkit.broadcastMessage("§c[능력] " + playerName + "님에게 " + abilityName + " 능력이 부여되었습니다!");
    }

    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        String abilityName = getClass().getSimpleName();
        String playerName = player.getName();

        // 전체에게 브로드캐스트
        Bukkit.broadcastMessage("§7[능력] " + playerName + "님의 " + abilityName + " 능력이 제거되었습니다.");
    }
}
