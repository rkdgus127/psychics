package Psychic.Core.Main;

import Psychic.Command.Pommand;
import Psychic.Command.PsyTabCompleter;
import Psychic.Core.AbilityClass.AbilityDamage.LevelForArmor;
import Psychic.Core.AbilityClass.AbilityDamage.LevelForDamage;
import Psychic.Core.AbilityClass.AbilityEffect.AbilityFireWorkDamage;
import Psychic.Core.Manager.Mana.Join;
import Psychic.Core.Manager.Mana.ManaManager;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

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
    @EventHandler
    public void onKnockback(EntityKnockbackByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;

        Entity damager = event.getHitBy();
        if (!(damager instanceof Snowball)) return;

        if (!damager.hasMetadata("noKnockback")) return;

        // 넉백 제거
        event.setKnockback(new Vector(0, 0, 0));
    }
}