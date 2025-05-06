package Psychic.Core.Abstract.Info;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbilityInfo {

    // 아이템 슬롯과 해당 아이템들
    private final Map<Integer, ItemStack> itemMap = new HashMap<>();

    public void openInfoInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, getTitle());

        // 등록된 능력 정보 아이템 넣기
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            gui.setItem(entry.getKey(), entry.getValue());
        }

        // GUI 꾸미기 적용
        decorateGUI(gui);

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



    private void decorateGUI(Inventory gui) {
        // 1번칸: → 이름의 엔더 크리스탈
        ItemStack next = new ItemStack(Material.END_CRYSTAL);
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName("§a→");
            next.setItemMeta(nextMeta);
        }
        gui.setItem(1, next);

        // 7번칸: ← 이름의 엔더 크리스탈
        ItemStack prev = new ItemStack(Material.END_CRYSTAL);
        ItemMeta prevMeta = prev.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName("§c←");
            prev.setItemMeta(prevMeta);
        }
        gui.setItem(7, prev);

        // 8번칸: 능력 정보입니다 이름의 가문비나무 표지판
        ItemStack info = new ItemStack(Material.SPRUCE_SIGN);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§e능력 정보입니다");
            info.setItemMeta(infoMeta);
        }
        gui.setItem(8, info);

    }
}
