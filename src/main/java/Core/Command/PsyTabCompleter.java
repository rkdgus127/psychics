package Core.Command;

import Core.AbilityConfig.Name;
import Core.Abstract.Ability;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PsyTabCompleter implements TabCompleter {
    private static final List<String> cachedAbilityNames = new ArrayList<>();
    private static boolean isInitialized = false;

    private void initializeCache() {
        if (isInitialized) return;

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader())
                        .setScanners(new SubTypesScanner(false))
        );


        cachedAbilityNames.addAll(
                reflections.getSubTypesOf(Ability.class).stream()
                        .filter(clazz -> !clazz.getName().contains("$"))
                        .filter(clazz -> clazz.isAnnotationPresent(Name.class))
                        .map(clazz -> clazz.getAnnotation(Name.class).value())
                        .sorted()
                        .collect(Collectors.toList())
        );

        isInitialized = true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!isInitialized) initializeCache();

        if (args.length == 1) {
            return Arrays.asList("attach", "remove", "info",  "enchant").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "attach", "info" -> {
                    return cachedAbilityNames.stream()
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "remove" -> {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(player -> player.getName())
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }

                case "enchant" -> {
                    return Arrays.asList("1", "2", "3", "4", "5").stream()
                            .filter(num -> num.startsWith(args[1]))
                            .collect(Collectors.toList());
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("attach")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
