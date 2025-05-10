package Psychic.Core.AbilityDamage;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public class AbilityDamage implements Listener {

    /*
    데미지 로직:
        보호 종류 1레벨당 상대의 보호 1을 무시 가능한 데미지
        초월 1 레벨당 0.12데미지
     */

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            // 플레이어의 방어구에서 보호 인챈트 레벨 확인
            int protectionLevel = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(item -> item != null && item.containsEnchantment(Enchantment.PROTECTION))
                    .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROTECTION)).sum();

            int fire = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(item -> item != null && item.containsEnchantment(Enchantment.FIRE_PROTECTION))
                    .mapToInt(item -> item.getEnchantmentLevel(Enchantment.FIRE_PROTECTION))
                    .sum();

            int projectile = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(item -> item != null && item.containsEnchantment(Enchantment.PROJECTILE_PROTECTION))
                    .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION))
                    .sum();

            int blast = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(item -> item != null && item.containsEnchantment(Enchantment.BLAST_PROTECTION))
                    .mapToInt(item -> item.getEnchantmentLevel(Enchantment.BLAST_PROTECTION))
                    .sum();

            int[] levels = PsychicsTag.getArmorTranscendLevels(player);
            int total = Arrays.stream(levels).sum(); // 전체 초월 레벨 합계


            double protection = player.getAttribute(Attribute.ARMOR).getValue();
            double t1 = (protectionLevel + protection + fire + projectile + blast) * 0.04;
            double t2 = (total * 0.12);
            if (t1 == 0 && t2 != 0) {
                t1 = 1;
            }
            if (t2 == 0 && t1 != 0) {
                t2 = 1;
            }
            double additionalDamage = event.getDamage() * t1 * t2;
            event.setDamage(event.getDamage() + additionalDamage);
        }
    }


    public static double PsychicDamage(Player player, double baseDamage) {
        int protectionLevel = Arrays.stream(player.getInventory().getArmorContents())
                .filter(item -> item != null && item.containsEnchantment(Enchantment.PROTECTION))
                .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROTECTION))
                .sum();

        int fire = Arrays.stream(player.getInventory().getArmorContents())
                .filter(item -> item != null && item.containsEnchantment(Enchantment.FIRE_PROTECTION))
                .mapToInt(item -> item.getEnchantmentLevel(Enchantment.FIRE_PROTECTION))
                .sum();

        int projectile = Arrays.stream(player.getInventory().getArmorContents())
                .filter(item -> item != null && item.containsEnchantment(Enchantment.PROJECTILE_PROTECTION))
                .mapToInt(item -> item.getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION))
                .sum();

        int blast = Arrays.stream(player.getInventory().getArmorContents())
                .filter(item -> item != null && item.containsEnchantment(Enchantment.BLAST_PROTECTION))
                .mapToInt(item -> item.getEnchantmentLevel(Enchantment.BLAST_PROTECTION))
                .sum();

        double protection = player.getAttribute(Attribute.ARMOR).getValue();

        int[] levels = PsychicsTag.getArmorTranscendLevels(player);
        int total = Arrays.stream(levels).sum();
        double t1 = (protectionLevel + protection + fire + projectile + blast) * 0.04;
        double t2 = (total * 0.12);
        if (t1 == 0 && t2 != 0) {
            t1 = 1;
        }
        if (t2 == 0 && t1 != 0) {
            t2 = 1;
        }
        return baseDamage + (baseDamage * t1 * t2);
    }
}