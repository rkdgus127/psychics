package rkdgus.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface Ability extends Listener {

    String getName();

    void onAttach(Player player);

    void onDetach(Player player);
}