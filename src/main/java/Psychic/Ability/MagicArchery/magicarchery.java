package Psychic.Ability.MagicArchery;

import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class magicarchery extends Ability {
    private final double mana = 40.0;
    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "&d매직 아처",
                    "&5마나 사용량: 40");
            addItem(2, Material.BOW, ChatColor.LIGHT_PURPLE + "&2일직선 활 PASSIVE",
                    "&5일직선으로 화살을 날립니다",
                    "&5화살이 적중한 적에게 데미지를 줍니다",
                    "&2기본 데미지: 4",
                    "&2풀차징 데미지: 8");
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow)) return;
        if (!AbilityManager.hasAbility(player, magicarchery.class)) return;
        if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;

        double force = event.getForce(); // 0.0 ~ 1.0
        if (ManaManager.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            event.setCancelled(true);
            return;
        }

        ManaManager.consume(player, mana);
        event.setConsumeItem(false);
        event.getProjectile().remove(); // 화살 제거
        event.setCancelled(true);

        World world = player.getWorld();
        Location start = player.getEyeLocation();
        // 발사 위치에 하얀색 파티클 생성
        world.spawnParticle(Particle.END_ROD, start, 50, 0.1, 0.1, 0.1, 0.01);
        Vector direction = start.getDirection().normalize();
        double range = 64 * force;
        final double baseDamage = (force >= 0.98) ? 8.0 : 4.0;
        final int level = Math.min(player.getLevel(), 40);
        final double multiplier = 1 + (level * 0.05);
        final double damage = baseDamage * multiplier;

        // 소리 먼저 재생
        world.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F + (float) Math.random() * 0.4F);

        new BukkitRunnable() {
            @Override
            public void run() {

                RayTraceResult result = world.rayTraceEntities(start, direction, range, 0.75, e ->
                        e instanceof LivingEntity && !e.equals(player));

                Location hitLoc = start.clone().add(direction.clone().multiply(range));
                double finalRange = range;

                if (result != null && result.getHitEntity() instanceof LivingEntity target) {
                    hitLoc = result.getHitPosition().toLocation(world);
                    finalRange = start.distance(hitLoc); // 히트 지점까지만 파티클

                    target.damage(damage, player);

                    FireworkEffect.Type effectType = FireworkEffect.Type.BURST;
                    Color color = (force >= 0.98) ? Color.RED : Color.YELLOW;
                    FireworkEffect effect = FireworkEffect.builder()
                            .with(effectType)
                            .withColor(color)
                            .flicker(true)
                            .build();

                    Firework firework = world.spawn(hitLoc, Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(effect);
                    meta.setPower(0);
                    firework.setFireworkMeta(meta);
                    firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
                    firework.detonate();
                }

                for (double d = 0; d < finalRange; d += 0.4) {
                    Location point = start.clone().add(direction.clone().multiply(d));
                    world.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0);
                }
            }
        }.runTaskLater(Psychic.getInstance(), 10L);
    }
}