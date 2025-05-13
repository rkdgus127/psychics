package Ability.Bomber;

import Core.AbilityConfig.Java.Config;
import Core.AbilityConfig.Java.Name;
import Core.AbilityDamage.AbilityDamage;
import Core.Abstract.Ability;
import Core.Abstract.PsychicInfo.AbilityInfo;
import Core.Abstract.PsychicInfo.Info;
import Core.Manager.Ability.AbilityManager;
import Core.Manager.CoolDown.Cool;
import Core.Manager.Mana.Mana;
import Core.Psychic;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

//봄버
@Name("bomber")
public class Bomber extends Ability {

    @Config
    public static double mana = 10.0;

    @Config
    public static int cool = 20;

    @Config
    public static boolean Active = true;

    @Config
    public static int damage = 5;

    @Config
    public static int selfDamage = 2;

    @Config
    public static int DeathDamage = 4;

    @Config
    public static Material wand = Material.GUNPOWDER;

    @Config
    public static int TNTRunTime = 3;

    @Config
    public static int Radius = 15;

    @Config
    public static int Speed = 2;

    @Info
    public static String damagePer = ChatColor.DARK_RED + "데미지: " + damage;

    @Info
    public static String self = ChatColor.AQUA + "본인에게 " + damage/selfDamage + "데미지";

    @Info
    public static String tnt = ChatColor.BLUE + "TNT 지속시간 : " + TNTRunTime + "초";

    @Info
    public static String selfAndDeath = ChatColor.GOLD+ "사망시 " + damage * DeathDamage + "데미지";

    @Config
    public static String description = "지정된 완드를 우클릭하여서 TNT를 머리 위에 소환시키고 신속을 부여 받습니다. " +
            "소환된 TNT는 머리 위로 따라다니며, " + TNTRunTime + "초 후에 터집니다. " +
            "TNT가 터지면 주변 " + Radius + "칸 내의 적에게 " + damage + "의 피해를 입히며, " +
            "본인에게는 " + damage / selfDamage + "의 피해를 입힙니다. " +
            "만약 능력 시전중 사망시 적들에게 " + DeathDamage + "배의 피해를 입힙니다.";

    public static class AI extends AbilityInfo {
        @Override
        public void setupItems() {
            autoSetupItems(Bomber.class);
        }
    }

    /*
    완드를 우클릭시 폭탄을 들고 달림
     */

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material playerItem = player.getInventory().getItemInMainHand().getType();
        if (playerItem == null || playerItem != wand) return;
        if (event.getAction().isRightClick()) {
            if (!AbilityManager.hasAbility(player, Bomber.class)) return;
            Cool.Check(player, wand);
            Mana.consume(player, mana);
            SutleRun(player);

            player.setCooldown(wand, cool * 20);
        }
    }

    public void SutleRun(Player player){
        // TNT 생성
        TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 2, 0), TNTPrimed.class);
        tnt.setMetadata("Bomber", new FixedMetadataValue(Psychic.getInstance(), player.getUniqueId().toString()));
        tnt.setFuseTicks(TNTRunTime * 20);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TNTRunTime * 20, Speed, false, false));
        FuseTick(player);

        new BukkitRunnable() {

            int timer = 0;


            @Override
            public void run() {
                Location loc = player.getLocation().add(0, 3, 0);

                tnt.spawnAt(loc);

                if (timer == TNTRunTime * 20) {
                    TntExplode(player);
                    cancel();
                }
                if (player.isDead() || !player.isOnline()) {
                    DEATH(player);
                    tnt.remove();
                    cancel();
                }
                timer++;
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }

    /*
    폭탄 폭발 메소드들
     */

    public void TntExplode(Player player) {
        Location center = player.getLocation();
        World world = player.getWorld();
        double r = Math.max(1.0, Radius - 2.0);

        world.spawnParticle(
                Particle.EXPLOSION_EMITTER,
                center,
                (int) (r * r),
                r, r, r,
                0.0,
                null,
                true
        );
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);

        double radius = AbilityDamage.PsychicDamage(player, Radius);

        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity living && !living.equals(player)) {
                living.damage(AbilityDamage.PsychicDamage(player, damage), player);
            }
        }

        player.damage(AbilityDamage.PsychicDamage(player, (double) damage / selfDamage), player);
    }

    public void DEATH(Player player){
        Location center = player.getLocation();
        World world = player.getWorld();
        double r = Math.max(1.0, Radius - 2.0);

        world.spawnParticle(
                Particle.EXPLOSION_EMITTER,
                center,
                (int) (r * r),
                r, r, r,
                0.0,
                null,
                true
        );
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);

        double radius = AbilityDamage.PsychicDamage(player, Radius);

        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity living && !living.equals(player)) {
                living.damage(AbilityDamage.PsychicDamage(player, damage * DeathDamage), player);
            }
        }
    }

    /*
    심지 연출
     */

    public void FuseTick(Player player) {
        new BukkitRunnable() {
            double timer = TNTRunTime * 20;
            double Fuse = TNTRunTime;
            @Override
            public void run() {
                Fuse -= Fuse/timer;
                timer--;
                Location loc = player.getLocation().add(0, Fuse + 2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3);
                if (timer == 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    cancel();
                }
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }
}
