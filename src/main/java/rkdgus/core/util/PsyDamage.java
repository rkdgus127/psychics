package rkdgus.core.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.enchantments.Enchantment;

public class PsyDamage {

    public static double psydamage(Player player, double base) {

        double armor = player.getAttribute(org.bukkit.attribute.Attribute.ARMOR).getValue();
        double toughness = player.getAttribute(org.bukkit.attribute.Attribute.ARMOR_TOUGHNESS).getValue();

        int prot = getProtectionLevel(player.getInventory());

        // 🔥 핵심 배율 계산
        double multiplier = 1
                + (armor * 0.08)
                + (toughness * 0.04)
                + (prot * 0.15);

        return base * multiplier;
    }

    private static int getProtectionLevel(PlayerInventory inv) {
        int total = 0;

        for (ItemStack item : inv.getArmorContents()) {
            if (item == null) continue;

            total += item.getEnchantmentLevel(Enchantment.PROTECTION);
        }

        return total;
    }
}