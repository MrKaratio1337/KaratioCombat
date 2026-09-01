package pl.karatiodev.combat.config;

import eu.okaeri.configs.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigFactory {

    public static PluginConfig load(JavaPlugin plugin){
        return ConfigManager.create(PluginConfig.class, config -> {

        });
    }
}
