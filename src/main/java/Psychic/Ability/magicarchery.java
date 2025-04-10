package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Random;

public class magicarchery extends Ability {

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "&d&l매직 아처",
                    "&5&l마나 사용량: 40");
            addItem(2, Material.BOW, ChatColor.LIGHT_PURPLE + "&2&l일직선 활 PASSIVE",
                    "&5&l일직선으로 화살을 날립니다",
                    "&5&l화살이 적중한 적에게 데미지를 줍니다",
                    "&2&l기본 데미지: 4",
                    "&2&l풀차징 데미지: 8");
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow originalArrow)) return;
        if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;

        event.setCancelled(true);

        final boolean isFullyCharged = event.getForce() >= 1.98;

        if (ManaManager.get(player) < 40) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        ManaManager.consume(player, 40.0);

        // 화살 발사 (보이지 않게)
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(25));
        arrow.setShooter(player);
        arrow.setCustomName(isFullyCharged ? "charged" : "uncharged");
        arrow.setCritical(false);
        arrow.setDamage(0);
        arrow.setInvisible(true);
        arrow.setSilent(true);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player)) return;

        Entity hit = event.getHitEntity();
        if (!(hit instanceof LivingEntity target)) return;
        arrow.remove();

        String type = arrow.getCustomName();
        if ("uncharged".equals(type)) {
            // 노란 파티클
            Location loc = hit.getLocation().clone().add(0, 0.0, 0);
            Firework firework = hit.getWorld().spawn(loc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
            meta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.BURST)
                    .withColor(Color.YELLOW)
                    .flicker(true)
                    .build());
            meta.setPower(0);
            firework.setFireworkMeta(meta);

            // 폭죽이 바로 터지도록 설정
            firework.detonate();

            int level = Math.min(((Player) arrow.getShooter()).getLevel(), 40);
            double multiplier = 1 + (level * 0.05); // 10% per level
            target.damage(4.0 * multiplier, (Player) arrow.getShooter());
        } else if ("charged".equals(type)) {
            // 빨간 폭죽 효과
            Location loc = hit.getLocation().clone().add(0, 0.0, 0);
            Firework firework = hit.getWorld().spawn(loc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
            meta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.BURST)
                    .withColor(Color.RED)
                    .flicker(true)
                    .build());
            meta.setPower(0);
            firework.setFireworkMeta(meta);

            // 폭죽이 바로 터지도록 설정
            firework.detonate();
            int level = Math.min(((Player) arrow.getShooter()).getLevel(), 40);
            double multiplier = 1 + (level * 0.05); // 10% per level
            target.damage(8.0 * multiplier, (Player) arrow.getShooter());
        }
    }
}