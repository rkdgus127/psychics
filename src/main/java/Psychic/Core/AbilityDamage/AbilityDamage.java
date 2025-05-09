package Psychic.Core.AbilityDamage;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public class AbilityDamage implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            // 플레이어의 방어구에서 보호 인챈트 레벨 확인
            int protectionLevel = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(item -> item != null && item.containsEnchantment(Enchantment.PROTECTION))
                    .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROTECTION)).sum();

            if (protectionLevel > 0) {
                // 보호 레벨에 따라 추가 데미지 계산 (보호 1당 4% 추가 데미지)
                double additionalDamage = event.getDamage() * (protectionLevel * 0.04);
                event.setDamage(event.getDamage() + additionalDamage);
            }
        }
    }


    public static double PsychicDamage(Player player, double baseDamage) {
        int protectionLevel = Arrays.stream(player.getInventory().getArmorContents())
                .filter(item -> item != null && item.containsEnchantment(Enchantment.PROTECTION))
                .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROTECTION))
                .sum();

        return baseDamage * (protectionLevel * 0.04); // 보호 1당 4% 추가 데미지
    }
}