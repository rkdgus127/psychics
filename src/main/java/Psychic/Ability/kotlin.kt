package Psychic.Ability

import Psychic.Core.AbilityClass.Abstract.Ability
import Psychic.Core.AbilityClass.Abstract.AbilityInfo
import Psychic.Core.Main.Depend.Psychic
import Psychic.Core.Mana.Manager.ManaManager
import Psychic.Core.Manager.AbilityManager
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

//코틀린
class kotlin : Ability() {
    //능력 설명 클래스 꼭 static이여야함
    class Info : AbilityInfo() {
        //꼭 setupItems여야함
        override fun setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&lKotlin", "&7이것은 샘플 능력입니다.", "&7능력 개발을 위한 참고용입니다.")
            addItem(
                2, Material.STICK, "&2&l샘플 클래스 ACTIVE",
                "히트스캔 방식이 필요하신분들은 이 코드를 참조해 주세요.",
                "코틀린 형식으로 작성된 샘플이기 때문에 자바형식이 필요하시다면",
                "thanos클래스나 magicarchery클래스를 참조하세요"
            )
        }
    }


    //능력 작동 코드
    //if문을 제외하고 딱히 능력이 있는걸 탐지하는 코드는 없어도됌
    @EventHandler
    fun onPlayerIn(event: PlayerInteractEvent) {
        val player = event.player
        if (!event.action.toString().contains("RIGHT")) return
        if (!AbilityManager.hasAbility(player, kotlin::class.java)) return
        if (event.item == null || event.item!!.type != Material.STICK) return
        if (player.hasCooldown(Material.STICK)) {
            player.sendActionBar("§2§l쿨타임이 남아있습니다.")
            return
        }
        if (ManaManager.get(player) < 1) {
            player.sendActionBar("§9§l마나가 부족합니다!")
            return
        }
        if (player.getTargetEntity(64) == null) {
            player.sendActionBar("§c§l대상을 찾을 수 없습니다!")
            return
        }
        player.setCooldown(Material.STICK, 1 * 20)

        val entity = player.getTargetEntity(64)
        if (entity !is LivingEntity) {
            player.sendActionBar("§c§l살아있는 생명체만 대상으로 가능합니다!")
            return
        }
        val baseDamage = 3.0
        val level = player.level
        val damage = baseDamage * (1 + 0.1 * level)

        entity.damage(damage, player)

        firework(entity)
        ManaManager.consume(player, 1.0)
    }
    fun firework(entity: LivingEntity) {
        val firework: Firework = entity.getWorld().spawn<Firework>(entity.getLocation(), Firework::class.java)
        val meta = firework.fireworkMeta
        firework.setMetadata("noDamage", FixedMetadataValue(Psychic.getInstance(), true))
        meta.addEffect(
            FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(Color.RED)
                .flicker(true)
                .build()
        )
        meta.power = 0
        firework.fireworkMeta = meta


        // 폭죽이 바로 터지도록 설정
        firework.detonate()
        object : BukkitRunnable() {
            override fun run() {
                firework.detonate()
                cancel()
            }
        }.runTaskLater(Psychic.getInstance(), 20)
    }
}