package pl.karatiodev.combat.services;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.karatiodev.combat.CombatPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatService {

    private final CombatPlugin plugin;
    private final Map<UUID, BukkitRunnable> timers = new ConcurrentHashMap<>();
    private final Map<UUID, Long> messageSent = new ConcurrentHashMap<>();

    public CombatService(CombatPlugin plugin){
        this.plugin = plugin;
    }

    public void startCombat(Player player){
        if(player != null){
            UUID uuid = player.getUniqueId();

            cancelTimer(uuid);

            long now = System.currentTimeMillis();
            Long lastSent = this.messageSent.get(uuid);

            if(lastSent == null || now - lastSent > 1000L){
                this.messageSent.put(uuid, now);
            }
        }
    }

    public void cancelTimer(UUID uuid){
        BukkitRunnable timer = this.timers.remove(uuid);
        if(timer != null){
            timer.cancel();
        }
    }
}
