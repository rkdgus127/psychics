package Psychic.Ability.Vampire;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Psychic;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


@Name("vampire")
public class Vampire extends Ability {

    private static final NamespacedKey DAMAGE_KEY = new NamespacedKey(JavaPlugin.getPlugin(Psychic.class), "vampire_damage");

    @Config
    public static int Heal_Multy = 10;

    @Config
    public static boolean Active = false;

    @Config
    public static String description = "상대에게 공격시 피해량의 " + Heal_Multy + "%를 회복시켜주는 핏방울을 떨어트립니다.";

    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Vampire.class);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!AbilityManager.hasAbility(attacker, Vampire.class)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        double damage = event.getFinalDamage();
        double heal = damage * Heal_Multy/100;

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
        if (!AbilityManager.hasAbility(player, Vampire.class)) {
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