package Psychic.Ability;

import Psychic.Core.AbilityClass.Abstract.Ability;
import Psychic.Core.AbilityClass.Abstract.AbilityInfo;
import Psychic.Core.Main.Depend.Psychic;
import Psychic.Core.Mana.Manager.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class grap extends Ability {
    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2&l그랩",
                    "&5&l마나 사용량: 25");
            addItem(2, Material.BONE, "&5&l그랩 ACTIVE",
                    "&9&l뼈다귀를 좌클릭하여 보는 방향으로 ",
                    "&9&l화살을 날립니다.",
                    "&3&l화살에 맞은 적은 플레이어에게 TP됩니다.",
                    "&1&l쿨타임: 35초"
            );
        }
    }

    @EventHandler
    public void onPlayerIn(PlayerInteractEvent event) {
        // 좌클릭을 확인
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BONE) return;
        Player player = event.getPlayer();
        if (player.hasCooldown(Material.BONE)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }

        // 마나 확인
        if (ManaManager.get(player) < 25) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        // 마나 소모
        player.setCooldown(Material.BONE, 35 * 20);
        ManaManager.consume(player, 25.0);
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.75)); // 속도 적절하게 조절
        arrow.setShooter(player);
        arrow.setCritical(false);
        arrow.setGravity(false);
        arrow.setDamage(0); // 데미지는 우리가 직접 줌
        arrow.setGlowing(true);
        arrow.setCustomName("grap");
        arrow.setCustomNameVisible(false);
        Random random = new Random();
        double x = random.nextInt(10);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1.5f);

        // 파티클 트레일
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isOnGround()) {
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(
                        Particle.CRIT,  // or SPELL, ENCHANTMENT_TABLE 등으로 변경 가능
                        player.getLocation().clone().add(0, 1.0, 0),
                        (int) x, 0.1, 0.1, 0.1, 0.1
                );
                arrow.getWorld().spawnParticle(Particle.FIREWORK,
                        arrow.getLocation(),
                        1, 0.1, 0.1, 0.1); // 발사 시 파티클 효과);
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player)) return;

        Entity hit = event.getHitEntity();
        if (!(hit instanceof LivingEntity target)) return;
        String type = arrow.getCustomName();
        if ("grap".equals(type)) {
            arrow.remove();
            Player player = event.getEntity().getShooter() instanceof Player ? (Player) event.getEntity().getShooter() : null;
            target.spawnAt(player.getLocation());
            target.getWorld().spawnParticle(Particle.WITCH, target.getLocation(), 100, 0.1, 0.1, 0.1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
        }
    }
}