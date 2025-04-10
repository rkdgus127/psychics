package Psychic.Core.Main.Depend;

import Psychic.Command.Executer.Pommand;
import Psychic.Command.Tab.PsyTabCompleter;
import Psychic.Core.AbilityClass.AbilityLevel.LevelForArmor;
import Psychic.Core.AbilityClass.AbilityLevel.LevelForDamage;
import Psychic.Core.AbilityClass.Damage.AbilityFireWorkDamage;
import Psychic.Core.Main.GuiClicker.Gui;
import Psychic.Core.Main.KnockBack.KnockBack;
import Psychic.Core.Mana.Join.Join;
import Psychic.Core.Mana.Manager.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Psychic extends JavaPlugin implements Listener {
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
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new KnockBack(), this);
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