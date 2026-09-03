package pl.karatiodev.combat;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {

    private final CombatPlugin plugin;

    @Getter
    private String latestVersion;

    @Getter
    private boolean updateAvailable = false;

    @Getter
    private boolean checked = false;

    public UpdateChecker(CombatPlugin plugin){
        this.plugin = plugin;
    }

    public void checkForUpdates(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try{
                URI uri = URI.create("https://api.karatioverse.pl/plugins/karatiocombat/version.txt");

                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))){
                    latestVersion = reader.readLine();

                    if(latestVersion != null){
                        latestVersion = latestVersion.trim();

                        String currentVersion = plugin.getDescription().getVersion();

                        updateAvailable = !currentVersion.equalsIgnoreCase(latestVersion);

                        if(updateAvailable){
                            plugin.getLogger().warning("A new version is available!");
                            plugin.getLogger().warning("Current: " + currentVersion);
                            plugin.getLogger().warning("Latest: " + latestVersion);
                        } else{
                            plugin.getLogger().info("You are using the latest version! Enjoy ;)");
                        }
                    }
                }

                connection.disconnect();
            } catch (Exception e){
                plugin.getLogger().warning("Could not check for updates!");
            } finally {
                checked = true;
            }
        });
    }
}
