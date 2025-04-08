package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class fangs extends Ability {

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&l송곳니",
                    "&5&l마나 사용량: 15");
            addItem(2, Material.STICK, "&e&l송곳니 발사 ACTIVE",
                    "&2&l막대기를 우클릭하면",
                    "&2&l바라보는 방향으로 송곳니를 날립니다.",
                    "&6&l쿨타임: 1초");
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) return;
        if (player.hasCooldown(Material.STICK)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }

        if (ManaManager.get(player) < 15) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        ManaManager.consume(player, 15.0);
        player.setCooldown(Material.STICK, 20); // 1초

        // 처음 방향과 위치를 고정
        Vector direction = player.getLocation().getDirection().normalize();
        Location startLoc = player.getEyeLocation().clone();

        int range = 200;

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= range || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location spawnLoc = startLoc.clone()
                        .add(direction.clone().multiply(step + 0.25))
                        .add(0, -0.5, 0);

                EvokerFangs fangs = (EvokerFangs) player.getWorld().spawnEntity(spawnLoc, EntityType.EVOKER_FANGS);
                fangs.setOwner(player);

                step++;
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }
}
