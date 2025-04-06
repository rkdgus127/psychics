package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.Main.Psychic;
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

public class Bomber extends Ability {

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 우클릭 시 gunpowder 아이템을 사용할 때만 실행
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.GUNPOWDER) return;

        if (player.hasCooldown(Material.GUNPOWDER)) {
            return;
        }

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
                if (ticks >= 5 * 20 || player.isDead()) { // 5초 후 TNT 폭발
                    if (tnt != null) {
                        onTNTExplode(tnt, player);
                    }
                    cancel();
                    return;
                }

                // TNT 생성 (시전자의 머리 위에 위치)
                if (ticks == 0) {
                    tnt = player.getWorld().spawn(player.getLocation().add(0, 2, 0), TNTPrimed.class);
                    tnt.setFuseTicks(100); // 즉시 폭발
                    tnt.setCustomNameVisible(false);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }
                if (ticks == 20) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }
                if (ticks == 40) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }
                if (ticks == 60) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }
                if (ticks == 80) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.0f);
                }

                // TNT가 시전자의 머리 위에 따라다니도록 위치 이동
                if (tnt != null) {
                    Vector vector = player.getLocation().getDirection().multiply(0.1); // 머리 위에서 따라다니도록 조정
                    tnt.setVelocity(vector);
                    tnt.teleport(player.getLocation().add(0, 3, 0)); // 시전자 머리 위로 고정
                }

                ticks++;
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L); // 0.05초마다 실행
    }

    // TNT 폭발 시 데미지 및 범위 계산
    private void onTNTExplode(TNTPrimed tnt, Player player) {

        // 방어력에 따른 데미지 증가율 (25% 증가)
        double defense = player.getAttribute(org.bukkit.attribute.Attribute.ARMOR).getValue();

        double damageMultiplier = 1 + (defense * 0.25);

        // 방어력에 따른 범위 증가율 (10% 증가)
        double rangeMultiplier = 1 + (defense * 0.1);

        // 폭발 범위와 데미지 계산 (임의의 범위 값 설정)
        double explosionRadius = 4 * rangeMultiplier;
        double damage = 10 * damageMultiplier;

        // 주변에 있는 플레이어와 엔티티에 데미지 적용
        for (Entity entity : tnt.getNearbyEntities(explosionRadius, explosionRadius, explosionRadius)) {
            entity.getWorld().spawnParticle(Particle.EXPLOSION, entity.getLocation(), 1, explosionRadius, 0.5, 0.5, 0.5);
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(damage);
            }
        }
    }
}
