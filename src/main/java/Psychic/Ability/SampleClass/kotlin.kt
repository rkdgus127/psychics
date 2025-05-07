package Psychic.Ability.SampleClass

import Psychic.Core.AbilityConfig.Java.Config
import Psychic.Core.AbilityConfig.Java.Name
import Psychic.Core.Abstract.Ability
import Psychic.Core.Abstract.Info.AbilityInfo
import Psychic.Core.Main.Psychic
import Psychic.Core.Manager.Ability.AbilityManager
import Psychic.Core.Manager.CoolDown.Cool
import Psychic.Core.Manager.Mana.Mana
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

@Name("kotlin")
class kotlin : Ability() {

    @Config
    var mana = 1.0

    @Config
    var cooldown = 20


    class Info : AbilityInfo() {
        //꼭 setupItems여야함
        override fun setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&bKotlin", "&7이것은 샘플 능력입니다.", "&7능력 개발을 위한 참고용입니다.")
            addItem(
                2, Material.STICK, "&2샘플 클래스 ACTIVE",
                "히트스캔 방식이 필요하신분들은 이 코드를 참조해 주세요.",
                "코틀린 형식으로 작성된 샘플이기 때문에 자바형식이 필요하시다면",
                "magicarchery클래스를 참조하세요"
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
        Cool.Check(player, Material.STICK)
        Mana.consume(player, mana)
        if (player.getTargetEntity(64) == null) {
            player.sendActionBar("§c§l대상을 찾을 수 없습니다!")
            return
        }
        player.setCooldown(Material.STICK, cooldown)

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