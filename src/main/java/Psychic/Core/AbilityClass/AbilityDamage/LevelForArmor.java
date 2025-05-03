package Psychic.Core.AbilityClass.AbilityDamage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

//공격당할시 레벨에 따라서 방어력 조정
public class LevelForArmor implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            int level = Math.min(player.getLevel(), 40);
            double baseDamage = event.getDamage();
            double multiplier = 1 + (level * 0.05); // 10% per level
            event.setDamage(baseDamage / multiplier);
        }
    }
}