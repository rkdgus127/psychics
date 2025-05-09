package Psychic.Core.Manager.Mana;

import Psychic.Core.Psychic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Mana.removeAllBars();
        Mana.initAll(Psychic.getInstance());
    }
}
