package Psychic.Core.Abstract.PsychicInfo;

import Psychic.Core.AbilityConfig.Java.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbilityInfo {

    // 아이템 슬롯과 해당 아이템들
    private static final Map<Integer, ItemStack> itemMap = new HashMap<>();

    public static void openInfoInventory(Player player) {
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
    public static String getTitle() {
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

    public AbilityInfo() {
        // 기존 설정 초기화
        itemMap.clear();
        setupItems();
    }


    protected void autoSetupItems(Class<?> abilityClass) {
        try {
            itemMap.clear(); // 기존 아이템 초기화
            Field activeField = abilityClass.getDeclaredField("Active");
            boolean isActive = (boolean) activeField.get(null);

            Field manaField = null;
            if (isActive) {
                manaField = abilityClass.getDeclaredField("mana");
            }

            if (!isActive) {
                addItem(0, Material.ENCHANTED_BOOK, "§l&2" + abilityClass.getSimpleName());
            }
            if (isActive && manaField.isAnnotationPresent(Config.class)) {
                double manaValue = (double) manaField.get(null);
                addItem(0, Material.ENCHANTED_BOOK, "§l&2" + abilityClass.getSimpleName(),
                        "&5마나 사용량: " + manaValue);
            }

            // 1번 슬롯: wand 정보
            Field wandField = null;
            try {
                wandField = abilityClass.getDeclaredField("wand");
            } catch (NoSuchFieldException e) {
            }

            Material wandMaterial = (wandField != null) ? (Material) wandField.get(null) : Material.BOOK;

            List<String> lore = new ArrayList<>();

            if (isActive) {
                Field coolField = abilityClass.getDeclaredField("cool");
                int cool = (int) coolField.get(null);
                lore.add("§l&9쿨타임: " + cool + "초");

                try {
                    Field durationField = abilityClass.getDeclaredField("duration");
                    int duration = (int) durationField.get(null);
                    if (duration != 0) {
                        lore.add("§l&d지속시간: " + duration + "초");
                    }
                } catch (NoSuchFieldException ignored) {
                }
            }


            // @Info 어노테이션이 있는 필드들 처리
            for (Field field : abilityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Info.class)) {
                    String value = (String) field.get(null);
                    lore.add("§l" + value);
                }
            }

            // description 추가
            try {
                Field descField = abilityClass.getDeclaredField("description");
                if (descField.isAnnotationPresent(Config.class)) {
                    String description = (String) descField.get(null);
                    String[] lines = description.split("\\n"); // 줄바꿈 기준으로 분리
                    for (String line : lines) {
                        lore.add(ChatColor.GRAY + line);
                    }
                }
            } catch (NoSuchFieldException ignored) {
            }

            // Name에 모든 설정 추가
            String itemName = "§l&c" + abilityClass.getSimpleName() +
                    (isActive ? " ACTIVE" : " PASSIVE");
            addItem(2, wandMaterial, itemName, lore.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InfoInv(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, getTitle());

        // GUI 꾸미기 적용
        decorateGUI(gui);

        player.openInventory(gui);
    }

    private static void decorateGUI(Inventory gui) {
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