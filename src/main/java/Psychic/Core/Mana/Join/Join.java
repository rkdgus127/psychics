package Psychic.Core.Mana.Join;

import Psychic.Core.Main.Depend.Psychic;
import Psychic.Core.Mana.Manager.ManaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ManaManager.removeAllBars();
        ManaManager.initAll(Psychic.getInstance());
    }
}
