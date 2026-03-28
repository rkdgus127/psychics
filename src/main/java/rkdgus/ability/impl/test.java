package rkdgus.ability.impl;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import rkdgus.ability.psy;
import rkdgus.core.config.config;
import rkdgus.core.psychics;
import rkdgus.core.util.psyabb;

public class test extends psy {

    @config
    private Material wand = Material.STICK;

    @config
    private int cooldown = 60;

    @config
    private int explosion_power = 5;

    @config
    private double base_damage = 3;

    @config
    private int range = 10;

    @Override
    public String getName() {
        return "test".toLowerCase();
    }

    @Override
    public void onAttach(Player player) {
    }

    @Override
    public void onDetach(Player player) {
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {

        if (!psyabb.left(event, getOwner(), wand)) return;

        Player player = event.getPlayer();

        if (psyabb.hascool(player, wand)) return;

        event.setCancelled(true);

        var dir = player.getEyeLocation().getDirection().normalize();
        var start = player.getEyeLocation().clone();

        new org.bukkit.scheduler.BukkitRunnable() {

            double traveled = 0;
            final double max = range;
            final double step = 0.3; // 속도 (작을수록 느림)

            @Override
            public void run() {

                if (traveled >= max) {
                    cancel();
                    return;
                }

                start.add(dir.clone().multiply(step));
                traveled += step;

                // 운석 느낌 파티클 (좀 크게 + 흔적)
                player.getWorld().spawnParticle(
                        org.bukkit.Particle.FLAME,
                        start,
                        3,
                        0.1, 0.1, 0.1,
                        0
                );

                player.getWorld().spawnParticle(
                        org.bukkit.Particle.SMOKE,
                        start,
                        2,
                        0.05, 0.05, 0.05,
                        0
                );

                // 블럭 충돌
                if (start.getBlock().getType().isSolid()) {
                    explode(player, start);
                    cancel();
                    return;
                }

                // 엔티티 충돌
                for (Entity e : player.getWorld().getNearbyEntities(start, 0.5, 0.5, 0.5)) {
                    if (e == player) continue;

                    explode(player, start);
                    cancel();
                    return;
                }
            }

        }.runTaskTimer(psychics.getInstance(), 0, 1);

        psyabb.cool(player, wand, cooldown);
    }

    private void explode(Player player, org.bukkit.Location loc) {
        player.getWorld().createExplosion(
                loc,
                pdam(base_damage),
                false,
                false
        );
    }
}