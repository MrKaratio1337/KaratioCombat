package pl.karatiodev.combat.services;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.utilities.ChatUtility;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageService {

    private final CombatPlugin plugin;
    private final Map<UUID, BossBar> activeBossBars = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> lastTimerSecond = new ConcurrentHashMap<>();

    public MessageService(CombatPlugin plugin){
        this.plugin = plugin;
    }

    public void startCombatMessage(Player player){
        int combatDuration = this.plugin.getPluginConfig().getAntylogout().getTime();
        send(player, this.plugin.getPluginConfig().getAntylogout().getStart(), "<seconds>", String.valueOf(combatDuration));

        if(this.plugin.getPluginConfig().getAntylogout().getSettings().isBossbarTimer()){
            setupBossBar(player, combatDuration);
        }
    }

    public void endCombatMessage(Player player){
        send(player, this.plugin.getPluginConfig().getAntylogout().getEnd(), null, null);
        clearBossBar(player);
        this.lastTimerSecond.remove(player.getUniqueId());
    }

    public void timerCombatMessage(Player player, int seconds){
        update(player, this.plugin.getPluginConfig().getAntylogout().getStart(), "<seconds>", String.valueOf(seconds));

        if(this.plugin.getPluginConfig().getAntylogout().getSettings().isBossbarTimer()){
            updateBossBar(player, seconds);
        }
    }

    public void sendQuitInCombatMessage(Player player){
        String messageStr = this.plugin.getPluginConfig().getMessages().getLogoutDuringFight().replace("<player>", player.getName());
        this.plugin.getServer().broadcast(ChatUtility.parse(messageStr));
        clearBossBar(player);
        this.lastTimerSecond.remove(player.getUniqueId());
    }

    public void clearAllBossBars(){
        this.activeBossBars.forEach((uuid, bossBar) -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if(player != null) player.hideBossBar(bossBar);
        });
        this.activeBossBars.clear();
        this.lastTimerSecond.clear();
    }

    private void send(Player player, Iterable<String> messages, String placeholder, String value){
        for(String message : messages){
            if(message != null && !message.isEmpty()){
                String formatted = placeholder != null ? message.replace(placeholder, value) : message;
                send(player, formatted);
            }
        }
    }

    private void update(Player player, Iterable<String> messages, String placeholder, String value) {
        UUID uuid = player.getUniqueId();

        int currentSecond = Integer.parseInt(value);
        Integer lastSecond = this.lastTimerSecond.get(uuid);
        boolean secondChanged = lastSecond == null || !lastSecond.equals(currentSecond);

        for(String message : messages){
            if(message == null || message.isEmpty()) continue;

            String formattedMessage = placeholder != null ? message.replace(placeholder, value) : message;
            String[] parts = formattedMessage.split("]", 2);

            if(parts.length < 2 || parts[1].trim().isEmpty()) continue;

            String type = parts[0].replace("[", "").toUpperCase();
            if(!type.equals("ACTIONBAR") && !type.equals("BOSSBAR")){
                if(secondChanged){
                    send(player, formattedMessage);
                }
            } else{
                send(player, formattedMessage);
            }
        }

        if(secondChanged){
            this.lastTimerSecond.put(uuid, currentSecond);
        }
    }

    private void send(Player player, String message){
        String[] parts = message.split("]", 2);
        if(parts.length >= 2 && !parts[1].trim().isEmpty()){
            String type = parts[0].replace("[", "").toUpperCase();
            Component component = ChatUtility.parse(parts[1].trim());

            switch (type){
                case "ACTIONBAR" -> player.sendActionBar(component);
                case "BOSSBAR" -> {
                    BossBar bar = this.activeBossBars.get(player.getUniqueId());
                    if(bar != null) bar.name(component);
                }
                case "MSG" -> player.sendMessage(component);
                default -> this.plugin.getLogger().warning("Unknown message type: " + type);
            }
        } else{
            player.sendMessage(ChatUtility.parse(message));
        }
    }

    private void setupBossBar(Player player, int maxSeconds){
        clearBossBar(player);

        BossBar bar = BossBar.bossBar(
                Component.empty(),
                1.0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );
        player.showBossBar(bar);

        this.activeBossBars.put(player.getUniqueId(), bar);
    }

    private void updateBossBar(Player player, int seconds){
        BossBar bar = this.activeBossBars.get(player.getUniqueId());
        if(bar != null){
            float progress = (float) seconds / (float) this.plugin.getPluginConfig().getAntylogout().getTime();
            bar.progress(Math.max(0.0f, Math.min(1.0f, progress)));
        }
    }

    private void clearBossBar(Player player){
        UUID uuid = player.getUniqueId();

        BossBar bar = this.activeBossBars.remove(uuid);
        if(bar != null) player.hideBossBar(bar);
    }
}
