package Core;

import Core.AbilityConfig.ConfigManager;
import Core.AbilityConfig.Name;
import Core.AbilityDamage.AbilityDamage;
import Core.AbilityDamage.PsychicsEnchant;
import Core.AbilityEffect.AbilityFW;
import Core.AbilityEffect.AbilitySnowballKnockBack;
import Core.AbilityEffect.AbilityTNT;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.InfoGuiClickChecker;
import Core.Command.Pommand;
import Core.Command.PsyTabCompleter;
import Core.Manager.Mana.Mana;
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

        Mana.removeAllBars();
        Mana.initAll(this);
        new ConfigManager(this);  // ConfigManager 초기화 추가
        ConfigManager.reloadAllConfigs();


        reloadConfig();
        // 이벤트 등록
        getServer().getPluginManager().registerEvents(new Mana(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityFW(), this);
        Bukkit.getPluginManager().registerEvents(new InfoGuiClickChecker(), this);
        Bukkit.getPluginManager().registerEvents(new AbilitySnowballKnockBack(), this);
        Bukkit.getPluginManager().registerEvents(new Mana.Join(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityTNT(), this);
        Bukkit.getPluginManager().registerEvents(new PsychicsEnchant(), this);

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
        Mana.removeAllBars(); // 이거 추가
        reloadConfigs();
    }

    public void reloadConfigs() {
        ConfigManager.reloadAllConfigs();
    }


    public static Psychic getInstance() {
        return instance;
    }

}