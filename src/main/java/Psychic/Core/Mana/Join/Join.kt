package Psychic.Core.Mana.Join

import Psychic.Core.Main.Depend.Psychic
import Psychic.Core.Mana.Manager.ManaManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Join: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent?) {
        ManaManager.removeAllBars()
        ManaManager.initAll(Psychic.getInstance())
    }
}