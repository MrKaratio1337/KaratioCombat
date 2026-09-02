package pl.karatiodev.combat;

import dev.rollczi.litecommands.LiteCommands;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.combat.config.ConfigFactory;
import pl.karatiodev.combat.config.PluginConfig;
import pl.karatiodev.combat.listeners.CombatListener;
import pl.karatiodev.combat.services.CombatService;
import pl.karatiodev.combat.services.MessageService;

@Getter
public class CombatPlugin extends JavaPlugin {

    private LiteCommands<?> liteCommands;

    private PluginConfig pluginConfig;
    private MessageService messageService;
    private CombatService combatService;

    @Override
    public void onEnable() {
        this.pluginConfig = ConfigFactory.load(this);

        this.messageService = new MessageService(this);
        this.combatService = new CombatService(this);

        this.registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners(){
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new CombatListener(this), this);
    }
}
