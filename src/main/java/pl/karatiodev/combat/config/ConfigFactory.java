package pl.karatiodev.combat.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ConfigFactory {

    public static PluginConfig load(JavaPlugin plugin){
        return ConfigManager.create(PluginConfig.class, config -> {
            config.withConfigurer(new YamlSnakeYamlConfigurer());
            config.withBindFile(new File(plugin.getDataFolder(), "config.yml"));
            config.saveDefaults();
            config.load(true);
            config.save();
        });
    }
}
