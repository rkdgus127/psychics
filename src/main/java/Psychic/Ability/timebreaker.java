package Psychic.Ability;

import Psychic.Core.AbilityClass.Abstract.Ability;
import Psychic.Core.AbilityClass.Abstract.AbilityInfo;
import Psychic.Core.Main.Depend.Psychic;
import Psychic.Core.Mana.Manager.ManaManager;
import Psychic.Core.Manager.AbilityManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class timebreaker extends Ability {
    private final Map<UUID, Integer> oldNoDamageTicks = new HashMap<>();
    private final Set<UUID> active = new HashSet<>();
    private final double mana = 50.0;

    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&d&l타임브레이커",
                    "&5&l마나 사용량: 50");
            addItem(2, Material.CLOCK, "&d&l타임브레이커 ACTIVE"
            ,
                    "&d&l우클릭 시 5초간 공격속도 2배!",
                    "&d&l공격당한 적은 무적 시간이 없어짐!",
                    "&8쿨타임: 45초");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.CLOCK) return;
        if (!AbilityManager.hasAbility(player, timebreaker.class)) return;
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        if (player.hasCooldown(Material.CLOCK)) {
            player.sendActionBar("쿨타임이 남아있습니다!");
            return;
        }
        if (ManaManager.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            return;
        }

        ManaManager.consume(player, mana);
        player.setCooldown(Material.CLOCK, 45 * 20);

        active.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                restoreAllTargets();
            }
        }.runTaskLater(Psychic.getInstance(), 100); // 7.5초 뒤 실행
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr != null) {
            attr.setBaseValue(6); // 공격 속도 미친 듯이 빠르게
        }


        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    active.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                Location loc = player.getLocation().add(0, 1, 0);
                player.getWorld().spawnParticle(Particle.FLAME, loc.add(0,1,0), 25, 0.1,0.1,0.1);

                if (++ticks >= 100) { // 7.5초
                    active.remove(player.getUniqueId());
                    AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
                    if (attr != null) {
                        attr.setBaseValue(4.0); // 마인크래프트 기본값
                    }

                    cancel();
                }
            }
        }.runTaskTimer(Psychic.getInstance(), 0, 1);
    }


    private final Map<UUID, Integer> targetDefaultTicks = new HashMap<>();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;
        if (!active.contains(damager.getUniqueId())) return;

        UUID targetId = target.getUniqueId();

        if (!targetDefaultTicks.containsKey(targetId)) {
            targetDefaultTicks.put(targetId, target.getMaximumNoDamageTicks());
        }

        target.setMaximumNoDamageTicks(0);
        target.setNoDamageTicks(0);
    }

    // 능력 끝날 때 모든 타겟 복구 (예: 일괄적으로)
    public void restoreAllTargets() {
        for (UUID id : targetDefaultTicks.keySet()) {
            Entity entity = Bukkit.getEntity(id);
            if (entity instanceof LivingEntity le) {
                le.setMaximumNoDamageTicks(targetDefaultTicks.get(id));
            }
        }
        targetDefaultTicks.clear();
    }

}