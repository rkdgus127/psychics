package Core.AbilityDamage;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PsychicsEnchant implements Listener {

    /*
    인첸트 확률 구상도
    초월 1 -> 50%
    초월 2 -> 25%
    초월 3 -> 15%
    초월 4 -> 10%
    초월 5 -> 5%
     */

    /*
    **경고**
    * 이 코드들은 Ai가 99% 작성한 코드임으로
    * 제가 주석을 따로 넣지 않았습니다.
    * 코드 해석을 원하시는 분들은 따로 잘 해보세요.
    * 또한 모루로 합치는 기능은 버그를 너무나 유발해서 걍 빼버렸습니다.
     */

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (!isArmor(item.getType())) return;

        Random random = new Random();
        double chance = random.nextDouble();
        int level = 0;

        if (chance <= 0.50) {
            level = 1;
        } else if (chance <= 0.75) {
            level = 2;
        } else if (chance <= 0.85) {
            level = 3;
        } else if (chance <= 0.90) {
            level = 4;
        } else if (chance <= 0.95) {
            level = 5;
        }


        ItemStack updated = PsychicsTag.addTag(item.clone(), level);
        event.getItem().setItemMeta(updated.getItemMeta());
    }

    public static boolean isArmor(Material type) {
        return type.name().endsWith("_HELMET")
                || type.name().endsWith("_CHESTPLATE")
                || type.name().endsWith("_LEGGINGS")
                || type.name().endsWith("_BOOTS");
    }
}