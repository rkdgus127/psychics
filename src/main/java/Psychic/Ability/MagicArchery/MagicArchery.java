package Psychic.Ability.MagicArchery;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityDamage.AbilityDamage;
import Psychic.Core.AbilityEffect.AbilityFW;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Abstract.PsychicInfo.Info;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.Mana;
import Psychic.Core.Psychic;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Name("magicarchery")
public class MagicArchery extends Ability {

    @Config
    public static double mana = 40.0;

    @Config
    public static boolean Active = false;

    @Config
    public static int damage = 4;

    @Info
    public static String damager = ChatColor.GREEN + "데미지: " +  damage;

    @Config
    public static String description = "화살을 쏘면 일직선으로 0.5초 후에 날라갑니다. 풀차징시 데미지 2배";

    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(MagicArchery.class);
        }
    }


    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow)) return;
        if (!AbilityManager.hasAbility(player, MagicArchery.class)) return;
        if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;

        double force = event.getForce(); // 0.0 ~ 1.0
        if (Mana.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            event.setCancelled(true);
            return;
        }

        Mana.consume(player, mana);
        event.setConsumeItem(false);
        event.getProjectile().remove(); // 화살 제거
        event.setCancelled(true);

        World world = player.getWorld();
        Location start = player.getEyeLocation();
        // 발사 위치에 하얀색 파티클 생성
        world.spawnParticle(Particle.END_ROD, start, 50, 0.1, 0.1, 0.1, 0.01);
        Vector direction = start.getDirection().normalize();
        double range = 64 * force;
        final double baseDamage = (force >= 2.98) ? damage * 2 : damage;
        double damage = AbilityDamage.PsychicDamage(player, baseDamage);

        // 소리 먼저 재생
        world.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F + (float) Math.random() * 0.4F);

        new BukkitRunnable() {
            @Override
            public void run() {

                RayTraceResult result = world.rayTraceEntities(start, direction, range, 0.75, e ->
                        e instanceof LivingEntity && !e.equals(player));

                Location hitLoc;
                double finalRange = range;

                if (result != null && result.getHitEntity() instanceof LivingEntity target) {
                    hitLoc = result.getHitPosition().toLocation(world);
                    finalRange = start.distance(hitLoc); // 히트 지점까지만 파티클

                    target.damage(damage, player);


                    Color color = (force >= 1.98) ? Color.RED : Color.YELLOW;
                    AbilityFW.FW(target, FireworkEffect.Type.BURST, color, 0);
                }

                for (double d = 0; d < finalRange; d += 0.4) {
                    Location point = start.clone().add(direction.clone().multiply(d));
                    world.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0);
                }
            }
        }.runTaskLater(Psychic.getInstance(), 10L);
    }
}