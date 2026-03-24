package rkdgus.core.config;

import rkdgus.core.psychics;

import java.io.IOException;
import java.nio.file.*;

public class ConfigWatcher implements Runnable {

    private final psychics plugin;
    private WatchService watchService;

    public ConfigWatcher(psychics plugin) {
        this.plugin = plugin;
    }

    private long lastReload = 0;

    public void start() {
        try {
            Path path = plugin.getDataFolder().toPath().resolve("abilities");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();

        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        while (true) {
            try {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                    // 🔥 여기 추가
                    if (System.currentTimeMillis() - lastReload < 500) continue;
                    lastReload = System.currentTimeMillis();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getAbilityManager().reloadAll();

                        plugin.getLogger().info("Ability config 자동 리로드됨");
                    });
                }

                key.reset();

            } catch (InterruptedException ignored) {}
        }
    }
}