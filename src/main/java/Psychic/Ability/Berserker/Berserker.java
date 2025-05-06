package Psychic.Ability.Berserker;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.Info.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
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
    public static int cool = 60 * 20;

    @Config
    public static int duration = 25 * 20; // 25초


    public static Material wand = Material.BLAZE_ROD;


    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2버서커",
                    "&5마나 사용량: " + mana);
            addItem(2, wand, "&c분노 모드 ACTIVE",
                    "&2블레이즈 막대기를 우클릭시",
                    "&2잠시 격분 상태가 됩니다.",
                    "&9쿨타임: " + cool / 20 + "초",
                    "&a지속시간: " + duration / 20 + "초",
                    "&3신속 LVL.2",
                    "&4피해량 감소율: 50%",
                    "&5넉백 무시");

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
        if (player.hasCooldown(wand)) {
            player.sendActionBar("쿨타임이 남아있습니다: " + (int) + player.getCooldown(wand)/20 + "초");
            return;
        }

        // ✅ 마나가 부족하면 메시지 출력 후 리턴
        if (ManaManager.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            return;
        }

        // ✅ 마나 소모
        ManaManager.consume(player, mana);

        // 능력 발동
        UUID uuid = player.getUniqueId();
        active.add(uuid);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2, false, false));
        player.setCooldown(wand, cool);
        playAbilityEffects(player, duration); // 25초 동안 효과 지속

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
                if (ticks >=duration) {
                    active.remove(uuid);
                    cancel();
                }
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }


    public void playAbilityEffects(Player player, long durationTicks) {
        // 폭죽 파티클
        Location loc = player.getLocation().clone().add(0, 2.0, 0);
        Firework firework = player.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(Color.RED)
                .flicker(true)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);

        // 폭죽이 생성되면 즉시 폭발을 취소하고 데미지를 없앰
        firework.detonate();

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