package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class snowgatling extends Ability {
    private final Random random = new Random();
    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2&l스노우 개틀링",
                    "&5&l마나 사용량: 25");
            addItem(2, Material.SNOWBALL, "&5&l개틀링 ACTIVE",
                    "&2&l눈덩이를 좌클릭 하여 바라보는 방향으로",
                    "&2&l눈덩이를 발사합니다.",
                    "&3&l지속시간: 10초",
                    "&3&l쿨타임: 22.5초"
                    );
            addItem(3, Material.BOOK, "&5&l얼음 심장 PASSIVE",
                    "&2&l눈덩이를 발사하여 적을 맞추면",
                    "&2&l적에게 구속 효과를 줍니다.",
                    "&3&l확률: 5%",
                    "&a&l지속시간: 5초",
                    "&9&l구속 레벨 증가 확률: 10%",
                    "&b&l최대 레벨: 6"
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerIn(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.SNOWBALL) return;
        event.setCancelled(true);

        if (player.hasCooldown(Material.SNOWBALL)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }
        if (ManaManager.get(player) < 25) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        // ✅ 마나 소모
        ManaManager.consume(player, 25.0);
        player.setCooldown(Material.SNOWBALL, (int) (22.5 * 20));
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
                    Player player = (Player) attacker;
                    int level = Math.min(player.getLevel(), 40);
                    double multiplier = 1 + (level * 0.05); // 10% per level
                    entity.damage(0.015 * multiplier, (Entity) projectile.getShooter());
                    entity.setNoDamageTicks(0);
                }

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
