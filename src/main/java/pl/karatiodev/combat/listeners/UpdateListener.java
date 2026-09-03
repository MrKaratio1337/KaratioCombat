package pl.karatiodev.combat.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.utilities.ChatUtility;

@AllArgsConstructor
public class UpdateListener implements Listener {

    private final CombatPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(!player.hasPermission("karatiocombat.update")) return;

        if(!plugin.getUpdateChecker().isChecked()) return;
        if(!plugin.getUpdateChecker().isUpdateAvailable()) return;

        String currentVersion = plugin.getDescription().getVersion();
        String latestVersion = plugin.getUpdateChecker().getLatestVersion();

        player.sendMessage(ChatUtility.parse("<gray>[<aqua>KaratioCombat<gray>] <red>Your plugin is outdated!"));
        player.sendMessage(ChatUtility.parse("<gray>Current version: <red>" + currentVersion));
        player.sendMessage(ChatUtility.parse("<gray>Latest version: <green>" + latestVersion));
        player.sendMessage(ChatUtility.parse("<yellow>Download the latest version from Github or Modirinth!"));
    }
}
