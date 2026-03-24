package rkdgus.core;

import org.bukkit.plugin.java.JavaPlugin;
import rkdgus.core.command.PsychicsCommand;
import rkdgus.core.config.AbilityConfigManager;
import rkdgus.core.config.ConfigWatcher;
import rkdgus.core.manager.AbilityManager;
import rkdgus.core.registry.AbilityRegistry;

import java.io.File;

public final class psychics extends JavaPlugin {
    private ConfigWatcher configWatcher;

    private static psychics instance;

    private AbilityRegistry abilityRegistry;
    private AbilityManager abilityManager;
    private AbilityConfigManager configManager;


    @Override
    public void onEnable() {
        instance = this;
        configWatcher = new ConfigWatcher(this);
        configWatcher.start();

        abilityRegistry = new AbilityRegistry(this);
        abilityManager = new AbilityManager();
        configManager = new AbilityConfigManager(this);

        abilityRegistry.registerAll("rkdgus.ability.impl");

        PsychicsCommand cmd = new PsychicsCommand(this);
        getCommand("psy").setExecutor(cmd);
        getCommand("psy").setTabCompleter(cmd);
    }

    public static psychics getInstance() {
        return instance;
    }

    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public AbilityConfigManager getConfigManager() {

        return configManager;
    }

    // 🔥 핵심 (protected 우회)
    public File getPluginFile() {
        return this.getFile();
    }
}