package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class gomugomu extends Ability {

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "§6§l고무고무", "§5§l마나 사용량: 50");
            addItem(2, Material.LEATHER, "§2§l고무 피스톨 ACTIVE",
                    "§f§l가죽 우클릭 시 10초간 상호작용 거리 증가",
                    "§9§l1레벨당: 0.2칸",
                    "§5§l최대 레벨: 40레벨",
                    "§2§l쿨타임: 25초");
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.LEATHER) return;

        if (player.hasCooldown(Material.LEATHER)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }

        if (ManaManager.get(player) < 50) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        int level = Math.min(player.getLevel(), 40);
        double bonus = Math.min(level * 0.2,100);

        AttributeInstance blockAttr = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityAttr = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);

        double originalBlock = blockAttr.getBaseValue();
        double originalEntity = entityAttr.getBaseValue();

        blockAttr.setBaseValue(originalBlock + bonus);
        entityAttr.setBaseValue(originalEntity + bonus);

        ManaManager.consume(player, 50);
        player.setCooldown(Material.LEATHER, 25 * 20);

        player.spawnParticle(Particle.RAID_OMEN, player.getLocation().add(0,1,0), 100, 0.1, 0.1, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        showParticle(player);
        player.sendMessage("§2§l블럭 사거리: " + (originalBlock + bonus) + "칸");
        player.sendMessage("§2§l엔티티 사거리: " + (originalEntity + bonus) + "칸");

        new BukkitRunnable() {
            @Override
            public void run() {
                blockAttr.setBaseValue(originalBlock);
                entityAttr.setBaseValue(originalEntity);
            }
        }.runTaskLater(Psychic.getInstance(), 10 * 20);
    }

    private void showParticle(Player player) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 10 * 20) {
                    cancel();
                    return;
                }
                Location loc = player.getLocation().add(0, 1, 0);
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 2, 0.1, 0.1, 0.1);
                tick += 1;
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }
}
