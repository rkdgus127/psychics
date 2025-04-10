package Psychic.Ability

import Psychic.Core.AbilityClass.Abstract.Ability
import Psychic.Core.AbilityClass.Abstract.AbilityInfo
import Psychic.Core.Main.Depend.Psychic
import Psychic.Core.Mana.Manager.ManaManager
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

//코틀린
class kotlin : Ability() {
    //능력 설명 클래스 꼭 static이여야함
    class Info : AbilityInfo() {
        //꼭 setupItems여야함
        override fun setupItems() {
            addItem(0, Material.ENCHANTED_BOOK, "&b&lKotlin", "&7이곳은 샘플 능력입니다.", "&7능력 개발을 위한 참고용입니다.")
            addItem(
                2, Material.STICK, "&2&l샘플 클래스 ACTIVE",
                "이곳은 샘플 능력입니다.", "히트스캔 방식으로 구현해뒀습니다",
                "혹시나 능력을 쓸일이 있으시다면 이 코드 참조하시면 됩니다."
            )
        }
    }


    //능력 작동 코드
    //if문을 제외하고 딱히 능력이 있는걸 탐지하는 코드는 없어도됌
    @EventHandler
    fun onPlayerIn(event: PlayerInteractEvent) {
        val player = event.player
        if (!event.action.toString().contains("RIGHT")) return
        if (event.item == null || event.item!!.type != Material.STICK) return
        if (player.hasCooldown(Material.STICK)) {
            player.sendMessage("§2§l쿨타임이 남아있습니다.")
            return
        }
        if (ManaManager.get(player) < 1) {
            player.sendActionBar("§9§l마나가 부족합니다!")
            return
        }
        if (player.getTargetEntity(120) == null) {
            player.sendActionBar("§c§l대상을 찾을 수 없습니다!")
            return
        }

        val entity = player.getTargetEntity(120)
        if (entity !is LivingEntity) {
            player.sendActionBar("§c§l살아있는 생명체만 대상으로 가능합니다!")
            return
        }
        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)!!.value
        ManaManager.consume(player, 1.0)

        object : BukkitRunnable() {
            override fun run() {
                if (entity.isDead()) {
                    player.sendMessage("타겟 번호 " + entity.getName() + "제거 완료")
                    cancel()
                    return
                }
                val current = entity.health
                if (current <= 0) {
                    cancel()
                    return
                }
                entity.damage(maxHealth / 25, player)
                entity.noDamageTicks = 0
                entity.setVelocity(Vector(0, 0, 0))
                spawnRandomFirework(entity)
            }
        }.runTaskTimer(Psychic.getInstance(), 0L, 1L)
    }

    fun spawnRandomFirework(player: LivingEntity) {
        val loc = player.location.clone().add(0.0, 2.0, 0.0)
        val firework = player.world.spawn(loc, Firework::class.java)
        val meta = firework.fireworkMeta
        firework.setMetadata("noDamage", FixedMetadataValue(Psychic.getInstance(), true))
        meta.addEffect(
            FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(randomColors)
                .flicker(true)
                .build()
        )
        meta.power = 0
        firework.fireworkMeta = meta
        firework.detonate()
    }

    private val randomColors: List<Color?>
        get() {
            val random = Random()
            val count = 1 + random.nextInt(5)
            val colors: MutableList<Color?> = ArrayList()
            for (i in 0..<count) {
                colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
            }
            return colors
        }
}