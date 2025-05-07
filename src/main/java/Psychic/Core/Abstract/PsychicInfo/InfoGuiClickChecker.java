package Psychic.Core.Abstract.PsychicInfo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InfoGuiClickChecker implements Listener {
    @EventHandler
    public void onInfoInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getTitle().equals("PSYCHIC")) { // 너가 GUI 제목 정한 거에 따라 변경
            event.setCancelled(true); // 클릭 무시

            if (event.getSlot() == 1) { // 예시: 첫 번째 슬롯 클릭

                Inventory newInventory = player.getServer().createInventory(null, 9, "PSYCHIC");
                player.openInventory(newInventory); // 새로운 인벤토리 열기
            }
            if (event.getSlot() == 7) {
                //기존에 열려있던 인벤토리 다시 열기
                AbilityInfo.openInfoInventory(player);
            }
        }
    }
}
