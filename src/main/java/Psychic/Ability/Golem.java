package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Golem extends Ability {

    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&2&l골램",
                    "&5&l마나 사용량:",
                    "&2&l 낙하 상쇄시: 낙하 데미지");
            addItem(2, Material.BOOK, "&c&l골램 펀치 PASSIVE",
                    "&2&l공격의 백터를 수평에서 수직으로 변환합니다.");
            addItem(3,Material.BOOK, "&c&l골램 스탠스 PASSIVE",
                        "&2&l모든 넉백을 무시합니다.");
            addItem(4,Material.BOOK, "&c&l골램 착지 PASSIVE",
                    "&2&l낙하 데미지를 무시합니다.");
        }
    }


    @EventHandler
    public void PlayerNo(PlayerVelocityEvent event) {
        Player player = event.getPlayer();

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();

        // 비동기 작업을 위해 runTask를 사용
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
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) event.getEntity();
                Double damage = event.getDamage();
                if (ManaManager.get(player) < damage) {
                    player.sendActionBar("§9§l마나가 부족합니다!");
                    return;
                }

                // ✅ 마나 소모
                ManaManager.consume(player, damage);
                event.setCancelled(true); // 낙하 데미지 취소
            }
        }
    }
}