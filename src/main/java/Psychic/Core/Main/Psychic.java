package Psychic.Core.Main;

import Psychic.Core.AbilityDamage.LevelForArmor;
import Psychic.Core.AbilityDamage.LevelForDamage;
import Psychic.Core.AbilityEffect.AbilityFireWorkDamage;
import Psychic.Core.AbilityEffect.AbilitySnowballKnockBack;
import Psychic.Core.Command.Pommand;
import Psychic.Core.Command.PsyTabCompleter;
import Psychic.Core.Manager.Mana.Join;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Psychic extends JavaPlugin{
    private static Psychic instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("psy").setExecutor(new Pommand());
        getCommand("psy").setTabCompleter(new PsyTabCompleter());

        getLogger().info("Psychic 플러그인 활성화됨");

        ManaManager.removeAllBars();
        ManaManager.initAll(this);

        // 이벤트 등록
        getServer().getPluginManager().registerEvents(new ManaManager(), this);
        getServer().getPluginManager().registerEvents(new LevelForArmor(), this);
        Bukkit.getPluginManager().registerEvents(new LevelForDamage(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityFireWorkDamage(), this);
        Bukkit.getPluginManager().registerEvents(new Gui(), this);
        Bukkit.getPluginManager().registerEvents(new AbilitySnowballKnockBack(), this);
        Bukkit.getPluginManager().registerEvents(new Join(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Psychic 플러그인 비활성화됨");
        ManaManager.removeAllBars(); // 이거 추가
    }

    public static Psychic getInstance() {
        return instance;
    }

}