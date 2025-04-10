package Psychic.Core.Main.GuiClicker;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class Gui implements Listener {
    @EventHandler
    public void onInfoInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        if (event.getView().getTitle().equals("PSYCHIC")) { // 너가 GUI 제목 정한 거에 따라 변경
            event.setCancelled(true); // 클릭 무시
        }
    }
}
