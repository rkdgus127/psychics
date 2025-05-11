package Psychic.Ability.Striker;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.AbilityDamage.AbilityDamage;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.PsychicInfo.AbilityInfo;
import Psychic.Core.Abstract.PsychicInfo.Info;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.CoolDown.Cool;
import Psychic.Core.Psychic;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

//스트라이커
@Name("striker")
public class Striker extends Ability {

    public Player ACTIVE = null;

    public boolean SNEAKING = false;

    public boolean FC = false; // FALL - CANCEL

    @Config
    public static boolean Active = true;

    @Config
    public static double mana = 50.0;

    @Config
    public static int cool = 120;

    @Config
    public static int UPSPEED = 10;

    @Config
    public static int DOWNSPEED = 8;

    @Config
    public static int damagePer = 4;

    @Config
    public static Material wand = Material.FEATHER;

    @Config
    public static int debuffDuration = 5;

    @Config
    public static int Radius = 15;

    @Info
    public static String damage = ChatColor.DARK_RED + "데미지: 낙뎀 / " + damagePer;

    @Info
    public static String debuff = ChatColor.AQUA + "구속 및 어둠 지속 시간: " + debuffDuration + "초";

    @Config
    public static String description = "도끼를 들고 우클릭하여 공중으로 도약한뒤, 쉬프트를 눌러 그 위치에 고정한 우 좌클릭하여 바라보는 방향으로 돌진합니다. " +
            "이때 땅에 닿는다면 닿을때 받는 낙하 데미지를 상쇄하고 1/" + damagePer + "배 하여서 주변 " + Radius * Radius + "칸 안에 있는 적들에게 피해를 입힙니다. " +
            "또한 그 범위 내에 있는 적들에게 구속 및 어둠 효과를 부여합니다.";


    public static class AI extends AbilityInfo {
        @Override
        public void setupItems(){
            autoSetupItems(Striker.class);
        }
    }

    @EventHandler
    public void onAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!AbilityManager.hasAbility(player, Striker.class)) return;
        Material mainHandType = player.getInventory().getItemInMainHand().getType();
        if (getAllAxes().contains(mainHandType)) {
            if (event.getAction().isRightClick()) {
                Cool.Check(player, mainHandType);
                boolean canJump = true;
                for (int i = 1; i <= 100; i++) {
                    Block blockAbove = player.getLocation().add(0, i, 0).getBlock();
                    if (blockAbove.getType() != Material.AIR &&
                            blockAbove.getType() != Material.WATER &&
                            blockAbove.getType() != Material.LAVA) {
                        canJump = false;
                        player.sendMessage(ChatColor.GREEN + "위에 블럭이 존재합니다!");
                        break;
                    }
                }

                if (canJump) {
                    Cool.Check(player, mainHandType);
                    player.setVelocity(player.getLocation().getDirection().multiply(0).setY(UPSPEED));
                    player.sendActionBar(ChatColor.RED + "웅크리기하여서 고정하세요!");
                    ACTIVE = player;
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if (ticks == (UPSPEED * 4) ||
                                    !player.isOnline() ||
                                    player.isDead()) {
                                cancel();
                                if (player.isSneaking()) {
                                    return;
                                }
                                ACTIVE = null;
                            }
                            ticks++;
                        }
                    }.runTaskTimer(Psychic.getInstance(), 0, 1);
                }
                player.setCooldown(mainHandType, cool * 20);
            }
        }
    }

    @EventHandler
    public void onPlayerSneaking(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ACTIVE != null && ACTIVE.equals(player)) {
            if (player.isSneaking()) {
                player.setVelocity(new Vector(0, 0.05, 0));
                player.sendActionBar(ChatColor.GREEN + "좌클릭하여 바라보는 방향으로 돌진하세요!");
                SNEAKING = true;
            }
        }
    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (ACTIVE != null && ACTIVE.equals(player)) {
            if (SNEAKING) {
                if (event.getAction().isLeftClick()) {
                    player.setVelocity(player.getLocation().getDirection().multiply(DOWNSPEED));
                    SNEAKING = false;
                    ACTIVE = null;
                    FC = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline() ||
                                    player.isDead() ||
                                    player.isOnGround()
                            ){
                                FC = false;
                                cancel();
                            }
                        }
                    }.runTaskTimer(Psychic.getInstance(), 0, 1);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!AbilityManager.hasAbility(player, Striker.class)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && FC) {

            if (player.isOnGround()) {
                Location center = player.getLocation();
                World world = player.getWorld();

                world.strikeLightningEffect(center);

                Material blockType = center.clone().subtract(0, 1, 0).getBlock().getType();

                for (double x = -Radius; x <= Radius; x += 1) {
                    for (double z = -Radius; z <= Radius; z += 1) {
                        Location loc = center.clone().add(x, 0, z);
                        if (loc.distanceSquared(center) <= Radius * Radius) {
                            Location particleLoc = loc.clone().add(0, 1, 0);
                            world.spawnParticle(Particle.BLOCK_CRUMBLE, particleLoc, 5, 0.2, 0.2, 0.2, blockType.createBlockData());
                        }
                    }
                }

                for (Entity entity : world.getNearbyEntities(center, Radius, Radius, Radius)) {
                    if (entity instanceof LivingEntity living && !living.equals(player)) {
                        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * debuffDuration, 5));
                        living.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20 * debuffDuration, 0));
                        living.damage(AbilityDamage.PsychicDamage(player, event.getDamage()/damagePer), player);
                    }
                }
            }
            event.setDamage(0);
        }
    }

    //모든 종류의 도끼
    public static List<Material> getAllAxes() {
        List<Material> axes = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().endsWith("_AXE")) {
                axes.add(material);
            }
        }
        return axes;
    }
}
