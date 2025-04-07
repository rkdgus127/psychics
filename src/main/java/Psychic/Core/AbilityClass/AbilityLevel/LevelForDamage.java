package Psychic.Core.AbilityClass.AbilityLevel;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LevelForDamage implements Listener {

    // 공격 시 레벨에 따라 데미지 보정
    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            int level = Math.min(player.getLevel(), 40);
            double baseDamage = event.getDamage();
            double multiplier = 1 + (level * 0.1); // 10% per level
            event.setDamage(baseDamage * multiplier);
        }
    }
}