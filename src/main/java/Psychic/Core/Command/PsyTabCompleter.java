package Psychic.Core.Command;

import Psychic.Core.Abstract.Ability;
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
                        .map(Class::getSimpleName)
                        .sorted()
                        .collect(Collectors.toList())
        );

        isInitialized = true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!isInitialized) {
            initializeCache();
        }

        if (args.length == 1) {
            return Arrays.asList("attach", "remove", "info", "know", "reload");
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "attach", "info" -> {
                    return cachedAbilityNames.stream()
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "remove", "know" -> {
                    return null;
                }
                case "reload" -> {}
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("attach")) {
            return null;
        }

        return Collections.emptyList();
    }
}
