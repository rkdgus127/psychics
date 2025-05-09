package Psychic.Core.Command;

import org.bukkit.entity.Player;

public class PlayerHolder {
    private static Player currentPlayer;

    public static void setPlayer(Player player) {
        currentPlayer = player;
    }

    public static Player getPlayer() {
        return currentPlayer;
    }
}