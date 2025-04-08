package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Berserker extends Ability {
    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2&l버서커",
                    "&5&l마나 사용량: 50");
            addItem(2, Material.BLAZE_ROD, "&c&l분노 모드 ACTIVE",
                    "&2&l블레이즈 막대기를 우클릭시",
                    "&2&l잠시 격분 상태가 됩니다.",
                    "&9&l쿨타임: 45초",
                    "&a&l지속시간: 25초",
                    "&3&l신속 LVL.2",
                    "&4&l피해량 감소율: 50%",
                    "&5&l넉백 무시"
                    );

        }
    }

    private final Set<UUID> active = new HashSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD) return;

        if (player.hasCooldown(Material.BLAZE_ROD)) return;

        // ✅ 마나가 부족하면 메시지 출력 후 리턴
        if (ManaManager.get(player) < 50) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        // ✅ 마나 소모
        ManaManager.consume(player, 50.0);

        // 능력 발동
        UUID uuid = player.getUniqueId();
        active.add(uuid);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25 * 20, 2, false, false));
        player.setCooldown(Material.BLAZE_ROD, 45 * 20);
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

    // 폭죽의 폭발 피해를 없애는 이벤트 처리
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Firework) {
            // 폭죽으로 인한 폭발을 취소하여 데미지 없애기
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && active.contains(player.getUniqueId())) {
            event.setDamage(event.getDamage() * 0.5);
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