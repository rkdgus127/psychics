package Ability.Vampire;

import Core.AbilityConfig.Java.Config;
import Core.AbilityConfig.Java.Name;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Manager.Ability.AbilityManager;
import Core.Psychic;
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

//뱀파이어
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

        // 공격시 주위에 레드스톤 드랍
        ItemStack bloodDrop = new ItemStack(Material.REDSTONE);
        ItemMeta meta = bloodDrop.getItemMeta();
        meta.setDisplayName("§4§l핏 방울");

        // 레드스톤에 데미지 입력
        meta.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.DOUBLE, heal);
        bloodDrop.setItemMeta(meta);

        // 레드스톤 드롭 및 이름
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

        // 뱀파이어만 줍기 가능
        // * 단 다른 뱀파이어도 스틸 가능 *
        if (!AbilityManager.hasAbility(player, Vampire.class)) {
            event.setCancelled(true);
            return;
        }

        // 저장된 데미지 출력
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