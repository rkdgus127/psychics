package Psychic.Core.AbilityDamage;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static Psychic.Core.AbilityDamage.PsychicsTag.getTranscendLevel;

public class PsychicsEnchant implements Listener {

    /*
    인첸트 확률 구상도
    초월 1 -> 50%
    초월 2 -> 25%
    초월 3 -> 15%
    초월 4 -> 10%
     */

    /*
    **경고**
    * 이 코드들은 Ai가 99% 작성한 코드임으로
    * 제가 주석을 따로 넣지 않았습니다.
    * 코드 해석을 원하시는 분들은 따로 잘 해보세요.
     */

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (!isArmor(item.getType())) return;

        Random random = new Random();
        double chance = random.nextDouble();
        int level = 0;

        if (chance <= 0.10) {
            level = 4;
        } else if (chance <= 0.25) {
            level = 3;
        } else if (chance <= 0.50) {
            level = 2;
        } else if (chance <= 1.00) {
            level = 1;
        }

        ItemStack updated = PsychicsTag.addTag(item.clone(), level);
        event.getItem().setItemMeta(updated.getItemMeta());
    }


    @EventHandler
    public void onAnvilCombine(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        if (first == null || second == null) return;
        if (!isArmor(first.getType()) || !isArmor(second.getType())) return;
        if (hasConflictingEnchants(first, second)) return;

        int level1 = getTranscendLevel(first);
        int level2 = getTranscendLevel(second);
        if (level1 == 0 && level2 == 0) return;

        ItemStack result = first.clone();

        if (level1 == level2 && level1 >= 1 && level1 <= 4) {
            result = PsychicsTag.addTag(result, level1 + 1);
            event.setResult(result);
        } else {
            int finalLevel = Math.max(level1, level2);
            result = PsychicsTag.addTag(result, finalLevel);
            event.setResult(result);
        }
    }


    public static boolean isArmor(Material type) {
        return type.name().endsWith("_HELMET")
                || type.name().endsWith("_CHESTPLATE")
                || type.name().endsWith("_LEGGINGS")
                || type.name().endsWith("_BOOTS");
    }

    public boolean hasConflictingEnchants(ItemStack item1, ItemStack item2) {
        for (Enchantment ench1 : item1.getEnchantments().keySet()) {
            for (Enchantment ench2 : item2.getEnchantments().keySet()) {
                if (ench1.conflictsWith(ench2)) {
                    return true;
                }
            }
        }
        return false;
    }

}
