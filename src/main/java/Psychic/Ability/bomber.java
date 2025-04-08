package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class bomber extends Ability {


    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&4&l봄버맨",
                    "&5&l마나 사용량:25");
            addItem(2, Material.GUNPOWDER, "&4&l봄버런 ACTIVE",
                    "&c&l폭탄을 들고 달립니다.",
                    "&2&l폭탄 지속시간: 5초",
                    "&3&l신속 LV.3",
                    "&1&l쿨타임: 15초",
                    "&5&l데미지:방어력에 비례하여 25%씩 증가");
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 우클릭 시 gunpowder 아이템을 사용할 때만 실행
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.GUNPOWDER) return;

        if (player.hasCooldown(Material.GUNPOWDER)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }
        // 마나 확인
        if (ManaManager.get(player) < 25) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        // 마나 소모
        ManaManager.consume(player, 25.0);

        // 쿨타임 설정 (15초)
        player.setCooldown(Material.GUNPOWDER, 15 * 20);

        // 5초 동안 속도 증가 효과 부여
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 3));

        // 5초 동안 TNT가 플레이어 머리 위에 따라다니게 함
        new BukkitRunnable() {
            int ticks = 0;
            TNTPrimed tnt = null;

            @Override
            public void run() {
                if (ticks >= 5 * 20 || player.isDead()) {
                    if (tnt != null) {
                        onTNTExplode(tnt, player);
                    }
                    cancel();
                    return;
                }

                if (ticks == 0) {
                    tnt = player.getWorld().spawn(player.getLocation().add(0, 2, 0), TNTPrimed.class);
                    tnt.setFuseTicks(120);
                    tnt.setCustomNameVisible(false);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }

                if (ticks % 20 == 0 && ticks < 100) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }

                if (tnt != null) {
                    Vector vector = player.getLocation().getDirection().multiply(0.1);
                    tnt.setVelocity(vector);
                    tnt.teleport(player.getLocation().add(0, 2.5, 0));
                }

                ticks++;
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }


    // TNT 폭발 시 데미지 및 범위 계산
    private void onTNTExplode(TNTPrimed tnt, Player player) {
        double defense = Math.min(player.getLevel(), 40);
        double damageMultiplier = 1 + (defense * 0.25);
        double rangeMultiplier = 1 + (defense * 0.05);

        double explosionRadius = 4 * rangeMultiplier;
        double damage = 10 * damageMultiplier;

        Location center = tnt.getLocation();
        tnt.remove(); // 기본 TNT 폭발 제거

        // 강하고 많은 폭발 파티클만
        center.getWorld().spawnParticle(Particle.EXPLOSION, center, 3);
        center.getWorld().spawnParticle(Particle.EXPLOSION, center, 100, explosionRadius, 0.5, explosionRadius, 0.2);

        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);

        for (Entity entity : tnt.getNearbyEntities(explosionRadius, explosionRadius, explosionRadius)) {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;

                if (living.equals(player)) {
                    living.damage(damage * 0.25); // 본인한테는 절반
                } else {
                    living.damage(damage);
                }
            }
        }
    }

}
