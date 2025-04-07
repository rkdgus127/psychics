package Psychic.Core.Main;

import Psychic.Command.Executer.Pommand;
import Psychic.Command.Tab.PsyTabCompleter;
import Psychic.Core.AbilityClass.AbilityLevel.LevelForDamage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class Psychic extends JavaPlugin implements Listener {
    private static Psychic instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("psy").setExecutor(new Pommand());
        getCommand("psy").setTabCompleter(new PsyTabCompleter());
        getLogger().info("Psychic 플러그인 활성화됨");
        Bukkit.getPluginManager().registerEvents(new LevelForDamage(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Psychic 플러그인 비활성화됨");
    }

    public static Psychic getInstance() {
        return instance;
    }


    @EventHandler
    public void onInfoInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        if (event.getView().getTitle().equals("PSYCHIC")) { // 너가 GUI 제목 정한 거에 따라 변경
            event.setCancelled(true); // 클릭 무시
        }
    }
}