package Psychic.Ability.SnowGatling;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.Info.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.ManaManager;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


@Name("snow-gatling")
public class SnowGatling extends Ability {


    private final Random random = new Random();
    
    @Config
    public static double mana = 25.0;
    
    @Config
    public static double duration = 5 * 20;
    
    @Config
    public static double cool = 20 * 22.5;


    public static Material wand = Material.SNOWBALL;
    
    @Config
    public static int Max_Slow_Level = 6;
    
    @Config
    public static int Slow_Level_Increase_Chance = 10;
    
    @Config
    public static int Slow_Chance = 5;
    
    
    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2스노우 개틀링",
                    "&5마나 사용량: 25");
            addItem(2, wand, "&5개틀링 ACTIVE",
                    "&2눈덩이를 좌클릭 하여 바라보는 방향으로",
                    "&2눈덩이를 발사합니다.",
                    "&3지속시간: 5초",
                    "&3쿨타임: 22.5초"
            );
            addItem(3, Material.BOOK, "&5얼음 심장 PASSIVE",
                    "&2눈덩이를 발사하여 적을 맞추면",
                    "&2적에게 구속 효과를 줍니다.",
                    "&3확률: 5%",
                    "&a지속시간: 5초",
                    "&9구속 레벨 증가 확률: 10%",
                    "&b최대 레벨: 6"
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerIn(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, SnowGatling.class)) return;
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != wand) return;
        event.setCancelled(true);

        if (player.hasCooldown(wand)) {
            player.sendActionBar("쿨타임이 남아있습니다: " + (int) + player.getCooldown(wand)/20 + "초");
            return;
        }
        if (ManaManager.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            return;
        }

        // ✅ 마나 소모
        ManaManager.consume(player, mana);
        player.setCooldown(wand, (int) cool);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration) {
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

        Snowball snowball = player.launchProjectile(Snowball.class, direction);
        snowball.setMetadata("noKnockback", new FixedMetadataValue(Psychic.getInstance(), true));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 0.075f, 1.0f);
    }

    private void applySlow(LivingEntity entity, int amplifier, int durationTicks) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, amplifier));
        entity.getWorld().spawnParticle(Particle.SNOWFLAKE, entity.getLocation(), 100, 0.4, 0.8, 0.4, 0.02);

    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Snowball)) return;
        if (!(event.getHitEntity() instanceof LivingEntity)) return;


        LivingEntity entity = (LivingEntity) event.getHitEntity();
        Player shooter = (projectile.getShooter() instanceof Player) ? (Player) projectile.getShooter() : null;
        if (!AbilityManager.hasAbility(shooter, SnowGatling.class)) return;

        // 데미지 로직
        if (shooter != null) {
            int level = Math.min(shooter.getLevel(), 40);
            double multiplier = 1 + (level * 0.05); // 5% per level
            entity.damage(0.001 * multiplier, shooter);
            entity.setNoDamageTicks(0);
        }

        // ❄️ 확률 적용
        if (random.nextInt(100) < Slow_Chance) { // 5% 확률로 구속
            applySlow(entity, 1, 100);

            // 🎯 레벨 증가 확률 10%
            if (random.nextInt(100) < Slow_Level_Increase_Chance) {
                PotionEffect current = entity.getPotionEffect(PotionEffectType.SLOWNESS);
                int currentAmp = current != null ? current.getAmplifier() : 0;
                int currentDuration = current != null ? current.getDuration() : 0;
                int newAmp = Math.min(currentAmp + 1, Max_Slow_Level);
                int newDuration = Math.max(currentDuration, 100); // 유지시간 더 길게
                applySlow(entity, newAmp, newDuration);
            }
        }
    }
}