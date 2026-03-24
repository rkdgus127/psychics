package rkdgus.core.util;

import rkdgus.core.psychics;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {

    public static List<Class<?>> getClasses(psychics plugin, String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        try {
            File file = plugin.getPluginFile();
            JarFile jar = new JarFile(file);

            String path = packageName.replace('.', '/');

            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                String name = entry.getName();

                if (!name.startsWith(path)) continue;
                if (!name.endsWith(".class")) continue;

                String className = name.replace("/", ".").replace(".class", "");

                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException ignored) {}
            }

            jar.close();

        } catch (IOException ignored) {}

        return classes;
    }
}