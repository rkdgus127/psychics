package Psychic.Ability.Berserker;

import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.AbilityInfo;
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

public class berserker extends Ability {
    private final double mana = 50.0;
    private final int duration = 60 * 20; // 25초
    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2버서커",
                    "&5마나 사용량: 50");
            addItem(2, Material.BLAZE_ROD, "&c분노 모드 ACTIVE",
                    "&2블레이즈 막대기를 우클릭시",
                    "&2잠시 격분 상태가 됩니다.",
                    "&9쿨타임: 60초",
                    "&a지속시간: 25초",
                    "&3신속 LVL.2",
                    "&4피해량 감소율: 50%",
                    "&4최대 80%",
                    "&4레벨에 따라서 증가",
                    "&5넉백 무시"
                    );

        }
    }

    private final Set<UUID> active = new HashSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, berserker.class)) return;
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD) return;
        event.setCancelled(true);
        if (player.hasCooldown(Material.BLAZE_ROD)) {
            player.sendActionBar("쿨타임이 남아있습니다: " + (int) + player.getCooldown(Material.BLAZE_ROD)/20 + "초");
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25 * 20, 2, false, false));
        player.setCooldown(Material.BLAZE_ROD, duration);
        playAbilityEffects(player, 25 * 20); // 25초 동안 효과 지속

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
                if (ticks >= 25 * 20) {
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
            int level = Math.min(player.getLevel(), 40);
            double reductionRatio = Math.min(level * 0.02, 0.8); // 최대 80%
            double finalDamage = event.getDamage() * (1 - reductionRatio);
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