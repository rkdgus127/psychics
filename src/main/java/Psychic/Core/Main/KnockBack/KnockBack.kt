package Psychic.Core.Main.KnockBack

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class KnockBack: Listener {
    @EventHandler
    fun onKnockback(event: EntityKnockbackByEntityEvent) {
        if (event.entity !is LivingEntity) return

        val damager = event.hitBy as? Snowball ?: return

        // 메타 체크 (옵션)
        if (!damager.hasMetadata("noKnockback")) return

        // 넉백 벡터를 0으로 설정 → 넉백만 제거됨
        event.knockback = Vector(0, 0, 0)
    }
}