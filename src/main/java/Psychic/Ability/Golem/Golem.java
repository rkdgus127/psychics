package Psychic.Ability.Golem;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.Mana;
import Psychic.Core.Psychic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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


    @EventHandler
    public void PlayerNo(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, Golem.class)) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        if (!(event.getDamager() instanceof Player player)) return;
        if (!AbilityManager.hasAbility(player, Golem.class)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                // 현재 속도를 가져옵니다
                Vector velocity = target.getVelocity();

                // 기존 수평 벡터 (x, z)
                double horizontalLength = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());

                // 수평 벡터를 수직으로 변환 (y 값에 수평 벡터의 길이를 적용)
                velocity.setX(0.0); // 수평 벡터의 X 값 제거
                velocity.setZ(0.0); // 수평 벡터의 Z 값 제거
                velocity.setY(horizontalLength); // 수평 벡터의 길이를 y 방향으로 설정

                // 수정된 속도를 설정
                target.setVelocity(velocity);
            }
        }.runTask(Psychic.getInstance()); // 여기서 yourPluginInstance는 플러그인 인스턴스를 넣어야 합니다.
    }

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