package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Fangs extends Ability {

    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&l송곳니",
                    "&5&l마나 사용량: 15");
            addItem(2, Material.STICK, "&e&l송곳니 발사",
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
        if (player.hasCooldown(Material.STICK)) return;

        if (ManaManager.get(player) < 15) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        ManaManager.consume(player, 15.0);
        player.setCooldown(Material.STICK, 20); // 0.25초

        Vector direction = player.getLocation().getDirection().normalize();
        int range = 200; // 최대 거리 (몇 개 소환할지)

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= range || !player.isOnline()) {
                    cancel();
                    return;
                }

                Vector spawnLoc = player.getEyeLocation()
                        .add(direction.clone().multiply(step + 0.25)) // step 0 = 1칸 앞
                        .add(0, -0.5, 0)
                        .toVector();

                EvokerFangs fangs = (EvokerFangs) player.getWorld().spawnEntity(
                        spawnLoc.toLocation(player.getWorld()),
                        EntityType.EVOKER_FANGS
                );
                step++;
                fangs.setOwner(player);
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L); // 1틱 간격
    }
}
