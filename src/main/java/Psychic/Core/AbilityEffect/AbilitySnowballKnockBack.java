package Psychic.Core.AbilityEffect;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class AbilitySnowballKnockBack implements Listener {

    @EventHandler
    public void onKnockback(EntityKnockbackByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;

        Entity damager = event.getHitBy();
        if (!(damager instanceof Snowball)) return;

        if (!damager.hasMetadata("noKnockback")) return;

        // 넉백 제거
        event.setKnockback(new Vector(0, 0, 0));
    }
}
