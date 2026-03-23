package rkdgus.core;

import rkdgus.core.command.PsychicsCommand;
import rkdgus.core.manager.AbilityManager;
import rkdgus.core.registry.AbilityRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class psychics extends JavaPlugin {

    private static psychics instance;

    private AbilityRegistry abilityRegistry;
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        instance = this;

        abilityRegistry = new AbilityRegistry();
        abilityManager = new AbilityManager();

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
}