package Psychic.Command.Tab;

import Psychic.Core.AbilityClass.Abstract.Ability;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PsyTabCompleter implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("attach", "remove", "info", "know");


    private List<String> getAbilityNames() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("Psychic.Ability"))
                        .setScanners(new SubTypesScanner(false))
        );
        Set<Class<? extends Ability>> classes = reflections.getSubTypesOf(Ability.class);


        List<String> list = classes.stream()
                .filter(clazz -> !clazz.getName().contains("$"))
                .map(Class::getSimpleName)
                .sorted()
                .collect(Collectors.toList());

        return list;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("attach") || args[0].equalsIgnoreCase("info")) {
                return getAbilityNames().stream()
                        .filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("know")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("attach")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
