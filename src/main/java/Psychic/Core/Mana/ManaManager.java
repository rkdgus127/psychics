package Psychic.Core.Mana;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ManaManager implements Listener {

    private static final HashMap<UUID, Integer> manaMap = new HashMap<>();
    private static final HashMap<UUID, NamespacedKey> keyMap = new HashMap<>();
    private static final int MAX_MANA = 100;

    public static void initAll(JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player, plugin);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : manaMap.keySet()) {
                    int current = manaMap.get(uuid);
                    int newMana = Math.min(MAX_MANA, current + 1);
                    manaMap.put(uuid, newMana);
                    updateBossBar(uuid);
                }
            }
        }.runTaskTimer(plugin, 0, 4); // 1초마다
    }

    public static void initPlayer(Player player, JavaPlugin plugin) {
        UUID uuid = player.getUniqueId();
        manaMap.put(uuid, MAX_MANA);

        NamespacedKey key = new NamespacedKey(plugin, "mana_" + uuid.toString());
        BossBar bar = Bukkit.createBossBar(key, "§b100 / 100", BarColor.BLUE, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        bar.addPlayer(player);

        keyMap.put(uuid, key);
    }

    public static boolean consume(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        if (!manaMap.containsKey(uuid)) return false;

        int current = manaMap.get(uuid);
        if (current < amount) return false;

        manaMap.put(uuid, (int) (current - amount));
        updateBossBar(uuid);
        return true;
    }

    public static int get(Player player) {
        return manaMap.getOrDefault(player.getUniqueId(), 0);
    }

    private static void updateBossBar(UUID uuid) {
        if (!manaMap.containsKey(uuid) || !keyMap.containsKey(uuid)) return;

        int mana = manaMap.get(uuid);
        BossBar bar = Bukkit.getBossBar(keyMap.get(uuid));
        if (bar != null) {
            bar.setProgress(Math.max(0.0, mana / 100.0));
            bar.setTitle("§b" + mana + " / 100");
        }
    }

    public static void removeAllBars() {
        for (UUID uuid : keyMap.keySet()) {
            NamespacedKey key = keyMap.get(uuid);
            BossBar bar = Bukkit.getBossBar(key);
            if (bar != null) {
                bar.removeAll(); // 플레이어 화면에서 제거
                Bukkit.removeBossBar(key); // 완전 제거!!
            }
        }
        manaMap.clear();
        keyMap.clear();
    }
}
