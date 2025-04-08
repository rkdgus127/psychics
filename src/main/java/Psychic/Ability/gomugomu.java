package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class gomugomu extends Ability {
    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "§6§l고무고무",
                    "§5§l마나 사용량: 5");
            addItem(2, Material.BOOK, "§2§l고무 피스톨 PASSIVE",
                    "§f§l검을 휘두를시에 레벨에 따라서",
                    "§a§l더 먼거리의 적을 공격합니다",
                    "§4§l1레벨당 증가율: 0.2칸",
                    "§9§l최대 레벨: 40",
                    "§b§l쿨타임: 2.5초");
        }
    }

    @EventHandler
    public void onSwordClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("LEFT")) return;

        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        Material type = main.getType();

        if (!isSword(type)) return;
        if (player.hasCooldown(type)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }

        if (ManaManager.get(player) < 5) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        event.setCancelled(true);
        ManaManager.consume(player, 5.0);
        player.setCooldown(type, (int) (2.5 * 20)); // 5초

        // 사거리 계산
        int level = Math.min(player.getLevel(), 40);
        double range = (double) level /5; // 1레벨 = 1칸

        Vector direction = player.getLocation().getDirection();
        RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), direction, range, 1.0, entity -> {
            return entity instanceof LivingEntity && entity != player;
        });

        if (result != null && result.getHitEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) result.getHitEntity();

            // 검 종류에 따라 피해량 설정
            double baseDamage = getDamageBySword(type);

            target.damage(baseDamage + baseDamage * ((double) level /10), player); // 검 피해량 적용
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
        }
    }
    private double getDamageBySword(Material material) {
        return switch (material) {
            case WOODEN_SWORD -> 4.0;
            case STONE_SWORD -> 5.0;
            case IRON_SWORD -> 6.0;
            case DIAMOND_SWORD -> 7.0;
            case NETHERITE_SWORD -> 8.0;
            default -> 5.0;
        };
    }


    private boolean isSword(Material material) {
        return material == Material.WOODEN_SWORD ||
                material == Material.STONE_SWORD ||
                material == Material.IRON_SWORD ||
                material == Material.DIAMOND_SWORD ||
                material == Material.NETHERITE_SWORD;
    }
}
