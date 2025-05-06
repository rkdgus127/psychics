package Psychic.Ability.TimeBreaker;

import Psychic.Core.AbilityConfig.Java.Config;
import Psychic.Core.AbilityConfig.Java.Name;
import Psychic.Core.Abstract.Ability;
import Psychic.Core.Abstract.AbilityInfo;
import Psychic.Core.Main.Psychic;
import Psychic.Core.Manager.Ability.AbilityManager;
import Psychic.Core.Manager.Mana.ManaManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Name("time-breaker")

public class TimeBreaker extends Ability {
    
    
    private final Map<UUID, Integer> oldNoDamageTicks = new HashMap<>();
    private final Set<UUID> active = new HashSet<>();
    
    @Config
    public static double mana = 50.0;
    
    @Config
    public static double cool = 900;

    @Config
    public static double duration = 100;

    @Config
    public static double Attack_Speed_Multy = 6.0;



    public static class Info extends AbilityInfo {
        @Override
        public void setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&d타임브레이커",
                    "&5마나 사용량: " + mana);
            addItem(2, wand, "&d타임브레이커 ACTIVE"
            ,
                    "&d우클릭 시 " + duration + "초간 공격속도 " + Attack_Speed_Multy / 4 + "배",
                    "&d공격당한 적은 무적 시간이 없어짐!",
                    "&8쿨타임: " + cool + "초");
        }
    }

    public static Material wand = Material.CLOCK;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != wand) return;
        if (!AbilityManager.hasAbility(player, TimeBreaker.class)) return;
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        if (player.hasCooldown(wand)) {
            player.sendActionBar("쿨타임이 남아있습니다: " + (int) player.getCooldown(wand)/20 + "초");
            return;
        }
        if (ManaManager.get(player) < mana) {
            player.sendActionBar("마나가 부족합니다: " + mana);
            return;
        }

        ManaManager.consume(player, mana);
        player.setCooldown(wand, (int) cool);

        active.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                restoreAllTargets();
            }
        }.runTaskLater(Psychic.getInstance(), (int) duration);
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr != null) {
            attr.setBaseValue(Attack_Speed_Multy); // 공격 속도 미친 듯이 빠르게
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

                if (++ticks >= duration) {
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