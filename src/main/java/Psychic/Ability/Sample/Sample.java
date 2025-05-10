package Psychic.Ability.Sample;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityDamage.AbilityDamage;
import Psychic.Core.AbilityEffect.AbilityFW;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Abstract.PsychicInfo.Info;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.CoolDown.Cool;
import Psychic.Core.Manager.Mana.Mana;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;


@Name("sample")
public class Sample extends Ability {

    @Config
    public static double mana = 10.0;

    @Config
    public static boolean Active = true;

    @Config
    public static int cool = 1;

    @Config
    public static int damage1 = 4;

    @Config
    public static int direction = 20; // 5초

    @Config
    public static Material wand = Material.STICK;

    @Info
    public static String damage = ChatColor.GOLD + "데미지: " + damage1;


    @Info
    public static String directionInfo = ChatColor.RED + "거리: " + direction;

    @Config
    public static String description = "스틱을 우클릭하면 적에게 데미지를 줍니다.";

    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Sample.class);
        }
    }


    @EventHandler
    public void onAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, Sample.class)) return;
        Cool.Check(player, wand);
        if (event.getAction().isRightClick()) {
            if (event.getItem() == null || event.getItem().getType() != wand) return;
            if (player.getTargetEntity(100) == null || !(player.getTargetEntity(100) instanceof LivingEntity)) return;
            LivingEntity target = (LivingEntity) player.getTargetEntity(100);
            target.damage(AbilityDamage.PsychicDamage(player, damage1), player);
            AbilityFW.FW(target, FireworkEffect.Type.STAR, Color.RED, 0);
            player.setCooldown(wand, (int) cool * 20);
            player.sendMessage("" + AbilityDamage.PsychicDamage(player,damage1));
            Mana.consume(player, mana);
        }
    }
}
