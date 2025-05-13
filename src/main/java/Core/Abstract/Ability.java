package Core.Abstract;

import Core.AbilityConcept;
import Core.Manager.Ability.AbilityManager;
import Core.Psychic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

public abstract class Ability implements AbilityConcept, Listener {

    @Override
    public void apply(Player player) {
        Bukkit.getPluginManager().registerEvents(this, Psychic.getInstance());
        String playerName = player.getName();
        var abilities = AbilityManager.getAbilities(player.getUniqueId());

        String abilityList = abilities.stream()
                .map(ability -> ability.getClass().getSimpleName())
                .collect(Collectors.joining(", "));
        Bukkit.broadcastMessage(ChatColor.GREEN + playerName + "'s Ability = " + abilityList);
    }

    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        String abilityName = getClass().getSimpleName();
        String playerName = player.getName();

        Bukkit.broadcastMessage(ChatColor.RED + playerName + "'s Ability-" + abilityName + " is gone");
    }
}
