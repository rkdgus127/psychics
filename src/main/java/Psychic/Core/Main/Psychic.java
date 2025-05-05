package Psychic.Core.Main;

import Psychic.Core.AbilityConfig.Java.ConfigManager;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityDamage.LevelForArmor;
import Psychic.Core.AbilityDamage.LevelForDamage;
import Psychic.Core.AbilityEffect.AbilityFireWorkDamage;
import Psychic.Core.AbilityEffect.AbilitySnowballKnockBack;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Command.Pommand;
import Psychic.Core.Command.PsyTabCompleter;
import Psychic.Core.Manager.Mana.Join;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

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

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader())
                        .setScanners(new SubTypesScanner(false))
        );

        Set<Class<? extends Ability>> abilityClasses = reflections.getSubTypesOf(Ability.class);
        for (Class<? extends Ability> abilityClass : abilityClasses) {
            try {
                if (abilityClass.isAnnotationPresent(Name.class)) {
                    Ability instance = abilityClass.getDeclaredConstructor().newInstance();
                    ConfigManager.loadConfig(instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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