package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class David extends Ability{


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


                // 창작 모드가 아닌 경우 아이템 갯수 차감
                if (player.getGameMode() != GameMode.CREATIVE) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }

                // 플레이어가 바라보는 방향으로 조약돌 던지기
                launchStone(player);
                player.setCooldown(Material.COBBLESTONE, (int) (0.25 * 20)); // 5초 쿨타임
            }
        }
    }

    // 능력 실행 (돌을 던짐)
    private void launchStone(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize().multiply(1);
        SmallFireball smallFireball = player.launchProjectile(SmallFireball.class, direction);
        smallFireball.setGravity(true);
        player.getWorld().playSound(location, Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);
    }
}