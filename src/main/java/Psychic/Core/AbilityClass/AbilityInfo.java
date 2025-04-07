package Psychic.Core.AbilityClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.*;

public abstract class AbilityInfo {

    // 아이템 슬롯과 해당 아이템들
    private final Map<Integer, ItemStack> itemMap = new HashMap<>();

    // GUI 열기 메소드
    public void openInfoInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, getTitle());

        // 등록된 아이템들을 GUI에 넣음
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            gui.setItem(slot, item);
        }

        player.openInventory(gui);
    }

    // GUI 제목
    public String getTitle() {
        return "PSYCHIC";
    }

    // 아이템 하나 등록
    protected void addItem(int slot, Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        itemMap.put(slot, item);
    }
    // 각 능력에 따라 아이템 등록
    public abstract void setupItems();

    // 생성자에서 자동으로 setupItems() 호출
    public AbilityInfo() {
        setupItems();
    }
}
