package pl.karatiodev.combat.services;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.karatiodev.combat.CombatPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatService {

    private final CombatPlugin plugin;
    private final Map<UUID, Integer> fights = new ConcurrentHashMap<>();
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
                this.plugin.getMessageService().startCombatMessage(player);
                this.messageSent.put(uuid, now);
            }

            final int duration = this.plugin.getPluginConfig().getAntylogout().getTime();
            this.fights.put(uuid, duration);

            BukkitRunnable task = new BukkitRunnable() {
                int time = duration;
                int quarterSeconds = 0;

                @Override
                public void run() {
                    if(CombatService.this.fights.containsKey(uuid) && player.isOnline()){
                        if(this.time <= 0){
                            CombatService.this.endCombat(player);
                            this.cancel();
                        } else{
                            CombatService.this.plugin.getMessageService().timerCombatMessage(player, this.time);
                            ++this.quarterSeconds;

                            if(this.quarterSeconds >= 4){
                                --this.time;
                                CombatService.this.fights.put(uuid, this.time);
                                this.quarterSeconds = 0;
                            }
                        }
                    } else{
                        this.cancel();
                    }
                }
            };
            task.runTaskTimer(this.plugin, 0, 5);
            this.timers.put(uuid, task);
        }
    }

    public void endCombat(Player player){
        if(player != null){
            UUID uuid = player.getUniqueId();

            cancelTimer(uuid);
            this.fights.remove(uuid);
            this.messageSent.remove(uuid);

            if(player.isOnline()){
                this.plugin.getMessageService().endCombatMessage(player);
            }
        }
    }

    public void handleQuit(Player player){
        if(player != null && isInCombat(player)){
            this.plugin.getMessageService().sendQuitInCombatMessage(player);
            player.setHealth(0.0);
            endCombat(player);
        }
    }

    public void clearAllCombats(){
        this.plugin.getServer().getOnlinePlayers().stream().filter(this::isInCombat).forEach(this::endCombat);
    }

    public boolean isInCombat(Player player){
        return player != null && this.fights.containsKey(player.getUniqueId());
    }

    public void cancelTimer(UUID uuid){
        BukkitRunnable timer = this.timers.remove(uuid);
        if(timer != null){
            timer.cancel();
        }
    }
}
