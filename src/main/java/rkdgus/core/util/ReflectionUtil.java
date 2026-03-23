package rkdgus.core.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resource = loader.getResource(path);

            if (resource == null) return classes;

            File dir = new File(resource.toURI());
            if (!dir.exists()) return classes;

            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String name = packageName + "." + file.getName().replace(".class", "");
                    classes.add(Class.forName(name));
                }
            }

        } catch (Exception ignored) {}

        return classes;
    }
}