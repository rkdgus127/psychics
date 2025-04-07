package Psychic.Core.Mana;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class ManaManager implements Listener {

    private static final HashMap<UUID, Integer> manaMap = new HashMap<>();
    private static final HashMap<UUID, BossBar> bossBarMap = new HashMap<>();

    private static final int MAX_MANA = 100;

    public static void initAll(JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player);
        }

        // 마나 자동 회복 태스크
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

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        manaMap.put(uuid, MAX_MANA);

        BossBar bar = Bukkit.createBossBar("§b마나: 100 / 100", BarColor.BLUE, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        bar.addPlayer(player);
        bossBarMap.put(uuid, bar);
    }

    public static boolean consume(Player player, Double amount) {
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
        if (!manaMap.containsKey(uuid) || !bossBarMap.containsKey(uuid)) return;

        int mana = manaMap.get(uuid);
        BossBar bar = bossBarMap.get(uuid);
        bar.setProgress(Math.max(0.0, mana / 100.0));
        bar.setTitle("§b마나: " + mana + " / 100");
    }
}
