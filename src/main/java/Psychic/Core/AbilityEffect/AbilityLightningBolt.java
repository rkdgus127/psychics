package Psychic.Core.AbilityEffect;

import org.bukkit.entity.LightningStrike;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AbilityLightningBolt implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LightningStrike) {
            if (event.getEntity().hasMetadata("noLightning")) {
                event.setCancelled(true);
            }
        }
    }
}
