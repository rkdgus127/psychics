package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.Main.Psychic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SnowGatling extends Ability {
    private final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerIn(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.SNOWBALL) return;

        if (player.hasCooldown(Material.SNOWBALL)) {
            return;
        }
        player.setCooldown(Material.SNOWBALL, (int) (22.5 * 20));
        event.setCancelled(true);
        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 10 * 20) {
                    cancel();
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    shootSnowball(player);
                }
                ticks++;
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }

    private void shootSnowball(Player player) {
        // 플레이어의 위치와 방향을 기준으로 눈덩이 발사
        org.bukkit.Location location = player.getLocation();
        org.bukkit.util.Vector direction = location.getDirection().multiply(2);

        // 눈덩이에 랜덤 변화를 주어 더 많이 퍼지도록 효과를 추가
        direction.add(new org.bukkit.util.Vector(
                random.nextDouble() * 0.5 - 0.5 / 2,
                random.nextDouble() * 0.5 - 0.5 / 2,
                random.nextDouble() * 0.5 - 0.5 / 2
        ));

        // 눈덩이 발사
        Snowball snowball = player.launchProjectile(Snowball.class, direction);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 0.075f, 1.0f);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Snowball) {
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event.getHitEntity();

                // 공격자의 방어력 계산
                double attackerArmor = 0;
                if (projectile.getShooter() instanceof Player) {
                    Player attacker = (Player) projectile.getShooter();
                    attackerArmor = attacker.getAttribute(org.bukkit.attribute.Attribute.ARMOR).getValue();
                }

                // 기본 데미지
                double baseDamage = 0.3;

                // 공격자의 방어력에 따라 데미지 증가 (방어력 1당 10% 증가)
                double additionalDamage = baseDamage * (attackerArmor * 0.25);  // 방어력 1당 10% 데미지 증가
                double finalDamage = baseDamage + additionalDamage;

                // 데미지 적용
                entity.damage(finalDamage);

                // 추가 효과 처리
                if (random.nextInt(100) < 1) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                    entity.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, entity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                    if (random.nextInt(100) < 5) {
                        PotionEffect slowEffect = entity.getPotionEffect(PotionEffectType.SLOWNESS);
                        if (slowEffect != null) {
                            int newLevel = Math.min(slowEffect.getAmplifier() + 1, 6);
                            entity.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, entity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, newLevel));
                        } else {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                            entity.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, entity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                        }
                    }
                }
            }
        }
    }
}
