package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier config.properties introuvable dans /resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
