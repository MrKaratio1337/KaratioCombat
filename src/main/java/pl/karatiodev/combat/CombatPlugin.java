package pl.karatiodev.combat;

import dev.rollczi.litecommands.LiteCommands;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.combat.config.ConfigFactory;
import pl.karatiodev.combat.config.PluginConfig;
import pl.karatiodev.combat.listeners.CombatListener;
import pl.karatiodev.combat.listeners.RegionListener;
import pl.karatiodev.combat.listeners.UpdateListener;
import pl.karatiodev.combat.services.CombatService;
import pl.karatiodev.combat.services.MessageService;
import pl.karatiodev.combat.services.RegionService;

@Getter
public class CombatPlugin extends JavaPlugin {

    private LiteCommands<?> liteCommands;

    private PluginConfig pluginConfig;
    private MessageService messageService;
    private CombatService combatService;
    private UpdateChecker updateChecker;
    private RegionService regionService;

    @Override
    public void onEnable() {
        this.pluginConfig = ConfigFactory.load(this);

        this.messageService = new MessageService(this);
        this.combatService = new CombatService(this);
        this.updateChecker = new UpdateChecker(this);
        this.regionService = new RegionService(this);

        updateChecker.checkForUpdates();

        this.registerListeners();
    }

    @Override
    public void onDisable() {
        if(this.combatService != null) this.combatService.clearAllCombats();
        if(this.liteCommands != null) this.liteCommands.unregister();
    }

    private void registerListeners(){
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new CombatListener(this), this);
        pluginManager.registerEvents(new UpdateListener(this), this);

        if(pluginManager.getPlugin("WorldGuard") != null){
            pluginManager.registerEvents(new RegionListener(this), this);
        }
    }
}
