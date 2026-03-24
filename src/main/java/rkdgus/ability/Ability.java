package rkdgus.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public interface Ability extends Listener {

    String getName();

    void onAttach(Player player);

    void onDetach(Player player);

    void setOwner(UUID uuid);

    UUID getOwner();
}