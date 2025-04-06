package Psychic.Core;

import Psychic.Command.Pommand;
import Psychic.Command.PsyTabCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Psychic extends JavaPlugin {
    private static Psychic instance;
    @Override
    public void onEnable() {
        getLogger().info("능력자 플러그인이 작동되었습니다!");
        instance = this;

        getCommand("psy").setExecutor(new Pommand());
        getCommand("psy").setTabCompleter(new PsyTabCommand());
    }
    @Override
    public void onDisable() {
        getLogger().info("무언가의 오류가 터졌습니다.");
    }
    public void reloadPlugin() {
        AbilityManager.clearAll();
        getLogger().info("Psychic 플러그인이 리로드되었습니다.");
    }

    public static Psychic getInstance() {
        return instance;
    }
}