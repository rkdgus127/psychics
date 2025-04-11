package Psychic.Ability;

import Psychic.Core.AbilityClass.Abstract.Ability;
import Psychic.Core.AbilityClass.Abstract.AbilityInfo;
import Psychic.Core.Main.Depend.Psychic;
import Psychic.Core.Manager.AbilityManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class vampire extends Ability {

    private static final NamespacedKey DAMAGE_KEY = new NamespacedKey(JavaPlugin.getPlugin(Psychic.class), "vampire_damage");

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&4&l뱀파이어");
            addItem(2, Material.BOOK, "&c&l핏 방울 PASSIVE",
                    "&c&l상대에게 피해를 주면",
                    "&c&l핏 방울을 떨어뜨립니다.",
                    "&2&l회복량: 데미지의 10%");
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!AbilityManager.hasAbility(attacker, vampire.class)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        double damage = event.getFinalDamage();
        double heal = damage * 0.1;

        // 핏방울 아이템 생성
        ItemStack bloodDrop = new ItemStack(Material.REDSTONE);
        ItemMeta meta = bloodDrop.getItemMeta();
        meta.setDisplayName("§4§l핏 방울");

        // 데미지 정보 저장
        meta.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.DOUBLE, heal);
        bloodDrop.setItemMeta(meta);

        // 아이템 드롭 + 이름 표시
        Item dropped = target.getWorld().dropItem(target.getLocation(), bloodDrop);
        dropped.setCustomName("§4§l핏 방울");
        dropped.setCustomNameVisible(true);

    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();

        if (!stack.hasItemMeta()) return;
        ItemMeta meta = stack.getItemMeta();
        if (!meta.getDisplayName().equals("§4§l핏 방울")) return;

        // 능력자 아닌데 줍기 시도 => 막음
        if (!AbilityManager.hasAbility(player, vampire.class)) {
            event.setCancelled(true);
            return;
        }

        // 저장된 회복량 꺼내기
        Double heal = meta.getPersistentDataContainer().get(DAMAGE_KEY, PersistentDataType.DOUBLE);
        if (heal == null) return;

        // 체력 회복
        double newHealth = Math.min(player.getHealth() + heal, player.getMaxHealth());
        player.setHealth(newHealth);
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);


        event.setCancelled(true);
        item.remove();
    }
}