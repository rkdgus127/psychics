package Psychic.Command.Tab;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class AbilityFinder {
    public static List<String> getAbilityNames() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("Psychic.Ability"))
                .setScanners(Scanners.SubTypes));

        return reflections.getSubTypesOf(Object.class).stream()
                .filter(c -> !c.getName().contains("$"))
                .map(Class::getSimpleName)
                .sorted()
                .collect(Collectors.toList());
    }
}
