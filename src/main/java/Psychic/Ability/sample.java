package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//자바로 능력을 개발하시는 분들을 위한 셈플 클래스
public class sample extends Ability {

    //능력 설명 클래스 꼭 static이여야함
    public static class Info extends AbilityInfo {

        //꼭 setupItems여야함
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&l샘플", "&7이곳은 샘플 능력입니다.", "&7능력 개발을 위한 참고용입니다.");
            addItem(2, Material.STICK, "&2&l샘플 클래스 ACTIVE",
                    "이곳은 샘플 능력입니다.", "히트스캔 방식으로 구현해뒀습니다",
                    "혹시나 능력을 쓸일이 있으시다면 이 코드 참조하시면 됩니다.");
        }
    }


    //능력 작동 코드
    //if문을 제외하고 딱히 능력이 있는걸 탐지하는 코드는 없어도됌
    @EventHandler
    public void onPlayerIn(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (event.getItem() == null || event.getItem().getType() != Material.STICK) return;
        if (player.hasCooldown(Material.STICK)) {
            player.sendMessage("§2§l쿨타임이 남아있습니다.");
            return;
        }
        if (ManaManager.get(player) < 50) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }
        if (player.getTargetEntity(100) == null) {
            player.sendActionBar("§c§l대상을 찾을 수 없습니다!");
            return;
        }

        Entity entity = player.getTargetEntity(100);
        if (!(entity instanceof LivingEntity target)) {
            player.sendActionBar("§c§l살아있는 생명체만 대상으로 가능합니다!");
            return;
        }
        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        ManaManager.consume(player, 50.0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isDead()) {
                    player.sendMessage("타겟 번호 " + target.getName() + "제거 완료");
                    cancel();
                    return;
                }
                double current = target.getHealth();
                if (current <= 0) {
                    cancel();
                    return;
                }
                target.damage(maxHealth / 25, player);
                target.setNoDamageTicks(0);
                target.setVelocity(new Vector(0, 0, 0));
                spawnRandomFirework(target);
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L);
    }

    public void spawnRandomFirework(LivingEntity player) {
        Location loc = player.getLocation().clone().add(0, 2.0, 0);
        Firework firework = player.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(getRandomColors())
                .flicker(true)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();
    }

    private List<Color> getRandomColors() {
        Random random = new Random();
        int count = 1 + random.nextInt(5);
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }
}
