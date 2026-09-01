package pl.karatiodev.combat;

import dev.rollczi.litecommands.LiteCommands;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.combat.config.ConfigFactory;
import pl.karatiodev.combat.config.PluginConfig;

@Getter
public class CombatPlugin extends JavaPlugin {

    private LiteCommands<?> liteCommands;

    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        this.pluginConfig = ConfigFactory.load(this);
    }

    @Override
    public void onDisable() {

    }
}
