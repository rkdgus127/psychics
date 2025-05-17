package Ability.SnowGatling;

import Core.AbilityConfig.Config;
import Core.AbilityConfig.Name;
import Core.AbilityDamage.AbilityDamage;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Abstract.PsychicInfo.Info;
import Core.Manager.Ability.AbilityManager;
import Core.Manager.CoolDown.Cool;
import Core.Manager.Mana.Mana;
import Core.Psychic;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

//스노우 게틀링 (스노우골램)
@Name("snow-gatling")
public class SnowGatling extends Ability {

    @Config
    public static double mana = 50.0;

    @Config
    public static boolean Active = true;

    @Config
    public static int duration = 5;

    @Config
    public static int cool = 20;

    @Config
    public static double speed = 1.2;

    @Config
    public static double wiggle = 0.5;

    @Config
    public static int count = 15;

    @Config
    public static double damage = 0.3;

    @Config
    public static int SlowChance = 75;

    @Config
    public static int SlowDuration = 10;

    @Config
    public static int StartLevel = 0;

    @Config
    public static int MaxLevel = 6;

    @Config
    public static int SlowUpChance = 50;

    @Config
    public static Material wand = Material.SNOWBALL;

    @Info
    public static String speed1 = ChatColor.DARK_RED + "속력: " + speed;

    @Info
    public static String wiggleInfo = ChatColor.GOLD + "퍼짐 범위: " + wiggle;

    @Info
    public static String countInfo = ChatColor.DARK_AQUA + "눈덩이 갯수: " + count;

    @Info
    public static String damageInfo = ChatColor.LIGHT_PURPLE + "데미지: " + damage;

    @Info
    public static String Chance = ChatColor.AQUA + "슬로우 확률: " + SlowChance + "%";

    @Info
    public static String SlowInfo = ChatColor.DARK_GRAY + "슬로우 지속시간: " + SlowDuration;

    @Info
    public static String LevelInfo = ChatColor.GREEN + "슬로우 레벨: " + StartLevel + " ~ " + MaxLevel;

    @Info
    public static String SlowUpChanceInfo = ChatColor.DARK_PURPLE + "슬로우 레벨 업 확률: " + SlowUpChance + "%";

    @Config
    public static String description = """
        지정된 완드를 우클릭 하여서 바라보는 방향으로 눈덩이를 사방으로 난사 합니다. 
        눈덩이에 맞은 적에게 %s 의 피해를 입히며 %s 의 확률로 %s 의 구속 효과를 줍니다. 
        구속 효과는 %s%%로 레벨이 1씩 증가하며 최대 %s 까지 증가합니다. 
        """.formatted(damage, SlowChance, StartLevel, SlowUpChance, MaxLevel);





    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(SnowGatling.class);
        }
    }

    /*
    눈덩이 우클릭시 눈덩이 발사
     */


    @EventHandler
    public void onAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, SnowGatling.class)) return;
        if (event.getItem() == null || event.getItem().getType() != wand) return;
        if (!event.getAction().isRightClick()) return;
        Cool.Check(player, wand);

        event.setCancelled(true);
        Mana.consume(player, mana);
        player.setCooldown(wand, (int) cool * 20);

        int localDuration = duration * 20;

        new BukkitRunnable() {
            int ticks = localDuration;

            @Override
            public void run() {
                if (ticks-- <= 0 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getEyeLocation();
                Vector direction = loc.getDirection().normalize().multiply(speed);

                for (int i = 0; i < count; i++) {
                    Vector offset = direction.clone().add(new Vector(
                            (Math.random() - 0.5) * wiggle,
                            (Math.random() - 0.5) * wiggle,
                            (Math.random() - 0.5) * wiggle
                    ));

                    Snowball snowball = player.launchProjectile(Snowball.class, offset);
                    snowball.setMetadata("noKnockBack", new FixedMetadataValue(Psychic.getInstance(), true));
                }

                loc.getWorld().playSound(loc, Sound.ENTITY_SNOWBALL_THROW, 0.3f, 1.2f);
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }


    /*
    눈덩이에 맞았을때 슬로우 + 데미지
     */

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball snowball && snowball.getShooter() instanceof Player player) {
            if (AbilityManager.hasAbility(player, SnowGatling.class)) {
                if (event.getEntity() instanceof LivingEntity target) {
                    event.setCancelled(true);
                    target.setNoDamageTicks(0);
                    target.setLastDamage(System.currentTimeMillis());
                    target.damage(AbilityDamage.PsychicDamage(player, damage), player);
                    ApplySlowness(target);
                }
            }
        }
    }


    public static void ApplySlowness(LivingEntity target) {
        Random random = new Random();
        PotionEffect current = target.getPotionEffect(PotionEffectType.SLOWNESS);

        if (current == null) {
            if (random.nextInt(100) < SlowChance) {
                PotionEffect newSlow = new PotionEffect(PotionEffectType.SLOWNESS, SlowDuration * 20, StartLevel);
                target.addPotionEffect(newSlow, true);
                SlowParticle(target);
            }
        } else {
            int amplifier = current.getAmplifier();
            if (amplifier < MaxLevel && random.nextInt(100) < SlowUpChance) {
                int newAmplifier = amplifier + 1;
                target.removePotionEffect(PotionEffectType.SLOWNESS);
                PotionEffect upgraded = new PotionEffect(PotionEffectType.SLOWNESS, SlowDuration * 20, newAmplifier);
                target.addPotionEffect(upgraded);
                SlowParticle(target);
            }
        }
    }


    public static void SlowParticle(LivingEntity target) {
        Location loc = target.getEyeLocation();
        loc.getWorld().spawnParticle(
                Particle.INSTANT_EFFECT,
                loc,
                125,
                0.7, 0.3, 0.7,
                0.01
        );
    }

}