package Psychic.Ability;

import Psychic.Core.AbilityClass.Abstract.Ability;
import Psychic.Core.AbilityClass.Abstract.AbilityInfo;
import Psychic.Core.Main.Depend.Psychic;
import Psychic.Core.Mana.Manager.ManaManager;
import Psychic.Core.Manager.AbilityManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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
        if (!(event.getProjectile() instanceof Arrow)) return;
        if (!AbilityManager.hasAbility(player, magicarchery.class)) return;
        if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;

        double force = event.getForce(); // 0.0 ~ 1.0
        if (ManaManager.get(player) < 40) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            event.setCancelled(true);
            return;
        }

        ManaManager.consume(player, 40.0);
        event.setConsumeItem(false);
        event.getProjectile().remove(); // 화살 제거
        event.setCancelled(true);

        World world = player.getWorld();
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        double range = 64 * force;
        double damage = (force >= 0.98) ? 8.0 : 4.0;
        int level = Math.min(player.getLevel(), 40);
        double multiplier = 1 + (level * 0.05);
        damage *= multiplier;

        Entity hit = null;
        Location hitLoc = start.clone().add(direction.clone().multiply(range));

        RayTraceResult result = world.rayTraceEntities(start, direction, range, 0.75, e ->
                e instanceof LivingEntity && !e.equals(player));

        if (result != null && result.getHitEntity() instanceof LivingEntity target) {
            hit = target;
            hitLoc = result.getHitPosition().toLocation(world);
            target.damage(damage, player);

            FireworkEffect.Type effectType = FireworkEffect.Type.BURST;
            Color color = (force >= 0.98) ? Color.RED : Color.YELLOW;
            FireworkEffect effect = FireworkEffect.builder().with(effectType).withColor(color).flicker(true).build();

            Firework firework = world.spawn(hitLoc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(effect);
            meta.setPower(0);
            firework.setFireworkMeta(meta);
            firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
            firework.detonate();
        }

        // 파티클 궤적 효과
        for (double d = 0; d < range; d += 0.4) {
            Location point = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0);
        }

        world.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F + (float)Math.random() * 0.4F);
    }

    private void spawnFirework(Location loc, boolean charged) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(charged ? Color.RED : Color.YELLOW)
                .with(FireworkEffect.Type.BURST)
                .flicker(true)
                .build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);
        fw.detonate();
    }



    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player)) return;
        if (!AbilityManager.hasAbility((Player) arrow.getShooter(), magicarchery.class)) return;

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