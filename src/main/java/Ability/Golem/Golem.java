package Ability.Golem;

import Core.AbilityConfig.Config;
import Core.AbilityConfig.Name;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Manager.Ability.AbilityManager;
import Core.Manager.Mana.Mana;
import Core.Psychic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

//골램
@Name("golem")
public class Golem extends Ability {

    @Config
    public static boolean Active = false;

    @Config
    public static String description = """
            모든 낙하 데미지를 마나로 변환합니다. 
            모든 넉백을 무시 합니다. 
            공격시 넉백의 백터를 수평에서 수직으로 변환합니다.""";


    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Golem.class);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        if (!(event.getDamager() instanceof Player player)) return;
        if (!AbilityManager.hasAbility(player, Golem.class)) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Vector velocity = target.getVelocity();
                double horizontalLength = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());

                velocity.setX(0.0);
                velocity.setZ(0.0);
                velocity.setY(horizontalLength);

                target.setVelocity(velocity);
            }
        }.runTaskLater(Psychic.getInstance(), 1L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!AbilityManager.hasAbility(player, Golem.class)) return;

        new BukkitRunnable() {
            public void run() {
                player.setVelocity(new Vector(0,0,0));
            }
        }.runTaskLater(Psychic.getInstance(), 1L);
    }


    //낙뎀 무시
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!AbilityManager.hasAbility(player, Golem.class)) return;

        double damage = event.getDamage();

        Mana.consume(player, damage);
        event.setCancelled(true);
    }
}