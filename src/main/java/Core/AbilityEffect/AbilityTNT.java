package Core.AbilityEffect;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class AbilityTNT implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            if (event.getDamager().hasMetadata("Bomber")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.TNT) {
            if (event.getEntity().hasMetadata("Bomber")) {
                event.setCancelled(true);
            }
        }
    }
}
