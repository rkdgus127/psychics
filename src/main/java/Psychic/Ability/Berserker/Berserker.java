package Psychic.Ability.Berserker;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityEffect.AbilityFW;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Abstract.PsychicInfo.Info;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.CoolDown.Cool;
import Psychic.Core.Manager.Mana.Mana;
import Psychic.Core.Psychic;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Name("berserker")
public class Berserker extends Ability {

    @Config
    public static double mana = 50.0;

    @Config
    public static boolean Active = true;

    @Config
    public static int cool = 60;

    @Config
    public static int duration = 25;
    @Config
    public static Material wand = Material.BLAZE_ROD;

    @Config
    public static int speed = 2;

    @Config
    public static int damageReduction = 50;

    @Info
    public static String damage = ChatColor.DARK_RED + "피해량 감소율: " + damageReduction + "%";

    @Info
    public static String knockback = ChatColor.GREEN + "넉백 무시";

    @Info
    public static String status = ChatColor.AQUA + "§o신속 LVL." + (speed + 1);

    @Config
    public static String description = "블레이즈 막대기를 우클릭시 잠시 격분 상태가 됩니다.";


    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Berserker.class);
        }
    }


    public final Set<UUID> active = new HashSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, Berserker.class)) return;
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != wand) return;
        event.setCancelled(true);
        Cool.Check(player, wand);
        // ✅ 마나 소모
        Mana.consume(player, mana);

        // 능력 발동
        UUID uuid = player.getUniqueId();
        active.add(uuid);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, speed, false, false));
        player.setCooldown(wand, cool * 20);
        playAbilityEffects(player, duration * 20L); // 25초 동안 효과 지속

        // 머리 위 파티클 표시 루프
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!active.contains(uuid) || !player.isOnline()) {
                    cancel();
                    return;
                }

                ticks++;
                if (ticks >=duration * 20) {
                    active.remove(uuid);
                    cancel();
                }
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }


    public void playAbilityEffects(Player player, long durationTicks) {
        AbilityFW.FW(player, FireworkEffect.Type.BALL_LARGE, Color.RED, 0);

        // 지속적으로 Angry Villager 파티클 띄우기
        new BukkitRunnable() {
            long ticks = 0;
            @Override
            public void run() {
                if (ticks >= durationTicks || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location particleLoc = player.getLocation().clone().add(0, 2.0, 0);
                player.getWorld().spawnParticle(
                        Particle.ANGRY_VILLAGER,
                        particleLoc,
                        4,
                        0.25, 0.0, 0.25, 0.0
                );

                ticks += 1;
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L); // 0.25초마다 실행
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && active.contains(player.getUniqueId())) {
            double finalDamage = event.getDamage() * (1 - 0.5); // 50% 감소
            event.setDamage(finalDamage);
        }
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if (active.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}