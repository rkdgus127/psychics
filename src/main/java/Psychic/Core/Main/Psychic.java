package Psychic.Core.Main;

import Psychic.Command.Executer.Pommand;
import Psychic.Command.Tab.PsyTabCompleter;
import Psychic.Core.AbilityClass.AbilityDamage.ArmorForDamage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Psychic extends JavaPlugin {
    private static Psychic instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("psy").setExecutor(new Pommand());
        getCommand("psy").setTabCompleter(new PsyTabCompleter());
        getLogger().info("Psychic 플러그인 활성화됨");
        Bukkit.getPluginManager().registerEvents(new ArmorForDamage(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Psychic 플러그인 비활성화됨");
    }

    public static Psychic getInstance() {
        return instance;
    }
}