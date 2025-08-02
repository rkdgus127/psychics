package Ability.MagicArchery;

import Core.AbilityConfig.Config;
import Core.AbilityConfig.Name;
import Core.AbilityDamage.AbilityDamage;
import Core.AbilityEffect.AbilityFW;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Abstract.PsychicInfo.Info;
import Core.Manager.Ability.AbilityManager;
import Core.Manager.Mana.Mana;
import Core.Psychic;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

//메직 아처
@Name("magic-archery")
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

        if (Mana.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            event.setCancelled(true);
            return;
        }

        Mana.consume(player, mana);
        event.setConsumeItem(false);
        event.getProjectile().remove(); // 화살 제거
        event.setCancelled(true);

        //차징 정도에 따라서 거리가 달라짐
        double force = event.getForce();
        World world = player.getWorld();
        Location start = player.getEyeLocation();
        world.spawnParticle(Particle.END_ROD, start, 50, 0.1, 0.1, 0.1, 0.01);
        Vector direction = start.getDirection().normalize();
        double range = 64 * force;

        // 풀 차징인지 아닌지 확인
        final double baseDamage = (force >= 0.98) ? damage * 2 : damage;
        double damage = AbilityDamage.PsychicDamage(player, baseDamage);

        world.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F + (float) Math.random() * 0.4F);

        //파티클 재생 및 맞는 효과 구현
        new BukkitRunnable() {
            @Override
            public void run() {

                RayTraceResult result = world.rayTraceEntities(start, direction, range, 0.75, e ->
                        e instanceof LivingEntity && !e.equals(player));

                Location hitLoc;
                double finalRange = range;

                if (result != null && result.getHitEntity() instanceof LivingEntity target) {
                    hitLoc = result.getHitPosition().toLocation(world);
                    finalRange = start.distance(hitLoc);

                    target.damage(damage, player);


                    Color color = (force >= 0.98) ? Color.RED : Color.YELLOW;
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