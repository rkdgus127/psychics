package Psychic.Core.AbilityClass.AbilityDamage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArmorForDamage implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();

        // 공격자가 플레이어일 경우
        if (damager instanceof Player) {
            Player attacker = (Player) damager;

            // 공격자의 방어력 계산
            double defense = attacker.getAttribute(org.bukkit.attribute.Attribute.ARMOR).getValue();

            // 기본 공격력
            double baseDamage = event.getDamage();

            // 공격자의 방어력에 따라 10%의 데미지를 추가
            double additionalDamage = defense * 0.25;  // 방어력 1당 10% 데미지 증가
            double adjustedDamage = baseDamage + additionalDamage;

            // 공격력 설정
            event.setDamage(adjustedDamage);
        }
    }
}