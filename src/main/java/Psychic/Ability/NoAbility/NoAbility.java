package Psychic.Ability.NoAbility;

import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.Info.AbilityInfo;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@Name("noability")
public class NoAbility extends Ability {
    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, ChatColor.AQUA + "무직자");
            addItem(2, Material.BOOK, ChatColor.GREEN + "무직자 PASSIVE", ChatColor.RED + "무직자의 특수함 힘으로 마나가 증가하지 않는다.",
                    "하지만 모든 데미지를 마나로 환산해서 마나가 깎이는걸로 받는다.",
                    ChatColor.GREEN + "마나가 없을 데미지를 받는다");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!AbilityManager.hasAbility(player, NoAbility.class)) return;
            ManaManager.consume(player, event.getDamage());
            ManaManager.setManaRegen(player, false);
            if (ManaManager.get(player) <= event.getDamage()) {
                return;
            }
            player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 10, 0.5, 0.5, 0.5);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            event.setCancelled(true);
        }
    }
}
