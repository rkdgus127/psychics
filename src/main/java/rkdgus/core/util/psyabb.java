package rkdgus.core.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class psyabb {

    public static boolean isOwner(Player player, UUID owner) {
        return player.getUniqueId().equals(owner);
    }

    public static boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isLeftClick(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean rightClick(PlayerInteractEvent e, UUID owner) {
        return isOwner(e.getPlayer(), owner) && isRightClick(e.getAction());
    }

    public static boolean leftClick(PlayerInteractEvent e, UUID owner) {
        return isOwner(e.getPlayer(), owner) && isLeftClick(e.getAction());
    }

    public static boolean isHolding(Player player, Material mat) {
        return player.getInventory().getItemInMainHand().getType() == mat;
    }

    public static boolean right(PlayerInteractEvent event, UUID owner, Material mat) {
        return rightClick(event, owner) && isHolding(event.getPlayer(), mat);
    }

    public static boolean left(PlayerInteractEvent event, UUID owner, Material mat) {
        return leftClick(event, owner) && isHolding(event.getPlayer(), mat);
    }

    public static void cool(Player player, Material mat, int ticks) {
        player.setCooldown(mat, ticks);
    }

    public static boolean hascool(Player player, Material mat) {
        return player.getCooldown(mat) > 0;
    }

}