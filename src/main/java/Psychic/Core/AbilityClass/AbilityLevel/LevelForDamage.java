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

    // 인첸트 시 종이 사용
    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        int cost = event.getEnchantmentBonus(); // 대략적인 레벨 요구치

        if (!consumePaper(player, cost)) {
            event.setCancelled(true);
            player.sendMessage("§c인첸트를 위해 종이 " + cost + "개가 필요합니다!");
        }
    }

    // 종이 소모 유틸
    private boolean consumePaper(Player player, int amount) {
        int paperCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER) {
                paperCount += item.getAmount();
            }
        }

        if (paperCount < amount) return false;

        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.PAPER) {
                int stackAmount = item.getAmount();
                if (stackAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= stackAmount;
                } else {
                    item.setAmount(stackAmount - remaining);
                    remaining = 0;
                    break;
                }
            }
        }
        return true;
    }
}