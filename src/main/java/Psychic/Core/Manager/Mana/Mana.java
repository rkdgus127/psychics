package Psychic.Core.Manager.Mana;

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


public class Mana implements Listener {

    private static final HashMap<UUID, Integer> manaMap = new HashMap<>();
    private static final HashMap<UUID, NamespacedKey> keyMap = new HashMap<>();
    private static final HashMap<UUID, Boolean> regenMap = new HashMap<>();
    private static final int MAX_MANA = 100;

    public static void initAll(JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player, plugin);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : manaMap.keySet()) {
                    if (!regenMap.getOrDefault(uuid, true)) continue; // 회복 차단된 유저는 스킵

                    int current = manaMap.get(uuid);
                    int newMana = Math.min(MAX_MANA, current + 1);
                    manaMap.put(uuid, newMana);
                    updateBossBar(uuid);
                }
            }
        }.runTaskTimer(plugin, 0, 1); // 1초마다
    }
    public static void setManaRegen(Player player, boolean allow) {
        regenMap.put(player.getUniqueId(), allow);
    }

    public static void initPlayer(Player player, JavaPlugin plugin) {
        UUID uuid = player.getUniqueId();
        manaMap.put(uuid, 0); // 초기 마나를 0으로 설정
        regenMap.put(uuid, true); // 기본은 자동 회복 허용
        NamespacedKey key = new NamespacedKey(plugin, "mana_" + uuid.toString());
        BossBar bar = Bukkit.createBossBar(key, "0 / " + MAX_MANA, BarColor.BLUE, BarStyle.SEGMENTED_10);
        bar.setProgress(0.0); // 마나가 0일 때 표시되는 progress
        bar.addPlayer(player);

        keyMap.put(uuid, key);
    }




    public static void consume(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        if (!manaMap.containsKey(uuid)) {
            throw new NoManaException();
        }

        int current = manaMap.get(uuid);
        if (current < amount) {
            player.sendActionBar("마나가 부족합니다: " + amount);
            throw new NoManaException();
        }

        manaMap.put(uuid, (int) (current - amount));
        updateBossBar(uuid);
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
            bar.setTitle(mana + " / " + MAX_MANA);
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