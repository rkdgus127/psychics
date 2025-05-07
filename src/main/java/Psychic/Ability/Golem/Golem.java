package Psychic.Ability.Golem;

import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.Info.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.Mana;
import org.bukkit.Material;
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


    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2골램",
                    "&5마나 사용량:",
                    "&2 낙하 상쇄시: 낙하 데미지");
            addItem(2, Material.BOOK, "&c골램 펀치 PASSIVE",
                    "&2공격의 백터를 수평에서 수직으로 변환합니다.");
            addItem(3,Material.BOOK, "&c골램 스탠스 PASSIVE",
                        "&2모든 넉백을 무시합니다.");
            addItem(4,Material.BOOK, "&c골램 착지 PASSIVE",
                    "&2낙하 데미지를 무시합니다.");
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