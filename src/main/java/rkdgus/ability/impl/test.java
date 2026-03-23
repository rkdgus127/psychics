package rkdgus.ability.impl;

import rkdgus.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class test implements Ability {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void onAttach(Player player) {
        player.sendMessage("test부여됌");
    }

    @Override
    public void onDetach(Player player) {
        player.sendMessage("test제거됨");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        player.sendMessage("test능력 발동");
    }
}