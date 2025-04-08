package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class David extends Ability{

    public static class Info extends AbilityInfo {

        @Override
        public void setupItems() {
            // 아이템 등록
            addItem(0, Material.ENCHANTED_BOOK, "&5&l화염술사",
                    "&5&l마나 사용량: 1");
            addItem(2, Material.COBBLESTONE, "&c&l불 던지기 ACTIVE",
                    "&2&l조약돌을 좌클릭하여 보는 방향으로 화염구를 던집니다.",
                    "&2&l조약돌을 1개 소모합니다.",
                    "&2&l쿨타임 0.1초");
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // 좌클릭을 확인
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.COBBLESTONE) {

                if (player.hasCooldown(Material.COBBLESTONE)) {
                    return;
                }

                if (ManaManager.get(player) < 1) {
                    player.sendActionBar("§9§l마나가 부족합니다!");
                    return;
                }
                // 마나 소모
                ManaManager.consume(player, 1.0);


                // 플레이어가 바라보는 방향으로 조약돌 던지기
                launchStone(player);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
                player.setCooldown(Material.COBBLESTONE, (int) (0.1 * 20)); // 5초 쿨타임
            }
        }
    }

    // 능력 실행 (돌을 던짐)
    private void launchStone(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize().multiply(0.5);
        SmallFireball smallFireball = player.launchProjectile(SmallFireball.class, direction);
        smallFireball.setGravity(true);
        player.getWorld().playSound(location, Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);
    }
}