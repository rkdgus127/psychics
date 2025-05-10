package Psychic.Core.AbilityDamage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PsychicsTag {

    /*
    초월 인첸트 적용 클래스
     */

    public static ItemStack addTag(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) return item;
        if (level < 1 || level > 6) throw new IllegalArgumentException("레벨은 1~5 사이여야 합니다.");

        String[] roman = {"I", "II", "III", "IV", "V"};
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        lore.removeIf(line -> ChatColor.stripColor(line).startsWith("초월"));

        ChatColor color = null;
        if (level - 1 < 2) {
            color = ChatColor.GRAY;
        } else if (level -1 < 4){
            color = ChatColor.GREEN;
        } else {
            color = ChatColor.GOLD;
        }

        lore.add(0, color + "§l초월 " + roman[level - 1]);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static int getTranscendLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return 0;

        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped.startsWith("초월 ")) {
                String level = stripped.substring("초월 ".length()).trim();
                return switch (level) {
                    case "I" -> 1;
                    case "II" -> 2;
                    case "III" -> 3;
                    case "IV" -> 4;
                    case "V" -> 5;
                    default -> 0;
                };
            }
        }
        return 0;
    }
    public static int[] getArmorTranscendLevels(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        int[] levels = new int[4]; // [헬멧, 흉갑, 레깅스, 부츠] 순

        for (int i = 0; i < armor.length; i++) {
            levels[i] = getTranscendLevel(armor[i]);
        }

        return levels;
    }
}
