package Core.AbilityEffect;

import Core.Psychic;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class AbilityFW implements Listener {
    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            if (firework.hasMetadata("noDamage")) {
                event.setCancelled(true);
            }
        }
    }

    public static void FW(LivingEntity target, FireworkEffect.Type effectType, Color color, int power) {

        boolean detonate = false;
        if (power == 0) {
            detonate = true;
        }
        Location loc = target.getLocation();
        FireworkEffect effect = FireworkEffect.builder()
                .with(effectType)
                .withColor(color)
                .flicker(true)
                .build();

        Firework firework = target.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(power);
        firework.setFireworkMeta(meta);
        firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
        if (detonate) {
            firework.detonate();
        }
    }
}