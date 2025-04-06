package Psychic.Command;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsyTabCommand implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("attach", "remove", "reload");
    private final List<String> abilities = Arrays.asList("fly", "fire", "speed", "heal");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("attach")) {
                return abilities.stream()
                        .filter(a -> a.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("remove")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(p -> p.getName())
                        .filter(name -> name.startsWith(args[1]))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("attach")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(p -> p.getName())
                    .filter(name -> name.startsWith(args[2]))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}