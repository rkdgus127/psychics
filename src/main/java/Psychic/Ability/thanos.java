package Psychic.Ability;

import Psychic.Core.AbilityClass.Ability;
import Psychic.Core.AbilityClass.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class thanos extends Ability {

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&l타노스",
                    "&5&l마나 사용량: 50");
            addItem(2, Material.ENDER_EYE, "&5&l절반 ACTIVE",
                    "&4&l타노스의 힘을 사용하여",
                    "&4&l상대방의 체력을 50%로 만듭니다.",
                    "&3&l쿨타임: 30초",
                    "&1&l사거리: 10칸");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.ENDER_EYE) return;

        event.setCancelled(true);

        if (player.hasCooldown(Material.ENDER_EYE)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다!");
            return;
        }

        if (ManaManager.get(player) < 50) {
            player.sendActionBar("§9§l마나가 부족합니다!");
            return;
        }

        LivingEntity target = (LivingEntity) player.getTargetEntity(10);
        if (target == null) {
            player.sendActionBar("§c§l대상을 찾을 수 없습니다!");
            return;
        }

        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        double halfHealth = maxHealth / 2.0;

        if (target.getHealth() <= halfHealth) {
            player.sendActionBar("§7§l이미 체력이 절반 이하입니다.");
            return;
        }

        // 마나 소비 및 쿨타임
        ManaManager.consume(player, 50.0);
        player.setCooldown(Material.ENDER_EYE, 30 * 20);

        // 체력을 1틱 간격으로 1씩 감소시키며 절반까지 도달시키기
        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isDead()) {
                    cancel();
                    return;
                }

                double current = target.getHealth();

                if (current <= halfHealth) {
                    cancel();
                    return;
                }

                target.damage(maxHealth/50, player); // 데미지 주기 (타노스로부터 받은 걸로 표시)
                target.setNoDamageTicks(0);
                spawnRandomFirework(target);
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L); // 0틱 대기 후, 1틱마다 실행
    }
    public void spawnRandomFirework(LivingEntity player) {
        Location loc = player.getLocation().clone().add(0, 2.0, 0);
        Firework firework = player.getWorld().spawn(loc, Firework.class);

        FireworkMeta meta = firework.getFireworkMeta();
        firework.setMetadata("noDamage", new FixedMetadataValue(Psychic.getInstance(), true));
        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(getRandomColors()) // 랜덤 색상들
                .flicker(true)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate(); // 즉시 폭발 (이전에 무해하게 설정된 상태여야 함)
    }

    // 랜덤 색상 리스트 반환
    private List<Color> getRandomColors() {
        Random random = new Random();
        int count = 1 + random.nextInt(5); // 최소 1개, 최대 5개 색상
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }
}
