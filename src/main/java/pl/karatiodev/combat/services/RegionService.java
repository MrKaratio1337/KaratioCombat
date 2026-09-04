package pl.karatiodev.combat.services;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.region.RegionBarrier;
import pl.karatiodev.combat.utilities.ChatUtility;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegionService {

    private final CombatPlugin plugin;
    private final Map<UUID, RegionBarrier> barriers = new ConcurrentHashMap<>();
    private final Map<UUID, Long> messageCooldowns = new ConcurrentHashMap<>();
    private static final long MESSAGE_COOLDOWN = 2000L;

    public RegionService(CombatPlugin plugin){
        this.plugin = plugin;
    }

    public void remove(Player player){
        if(player != null){
            UUID uuid = player.getUniqueId();

            RegionBarrier barrier = this.barriers.remove(uuid);
            if(barrier != null){
                barrier.hide();
            }
        }
    }

    public void forceRemove(Player player){
        if(player != null){
            UUID uuid = player.getUniqueId();

            RegionBarrier barrier = this.barriers.get(uuid);
            if(barrier != null){
                barrier.hide();
                this.barriers.remove(uuid);
            }
        }
    }

    public void send(Player player){
        if(player != null){
            long now = System.currentTimeMillis();

            UUID uuid = player.getUniqueId();
            if(!this.messageCooldowns.containsKey(uuid) || now - this.messageCooldowns.get(uuid) >= MESSAGE_COOLDOWN){
                player.sendMessage(ChatUtility.parse(this.plugin.getPluginConfig().getMessages().getCannotEnterRegion()));
                this.messageCooldowns.put(uuid, now);
            }
        }
    }

    public void show(Player player, RegionBarrier barrier){
        if(player != null && barrier != null){
            UUID uuid = player.getUniqueId();
            if(this.barriers.containsKey(uuid)){
                this.barriers.get(uuid).update(barrier.getCenter(), barrier.getRegion(), barrier.getDistance());
            } else{
                this.barriers.put(uuid, barrier);
                startBarrierUpdater(player);
            }
        }
    }

    private void startBarrierUpdater(Player player){
        if(player != null){
            new BukkitRunnable(){
                private UUID uuid = player.getUniqueId();
                private int counter = 0;

                @Override
                public void run() {
                    if(RegionService.this.barriers.containsKey(this.uuid) && player.isOnline()
                    && RegionService.this.plugin.getCombatService().isInCombat(player)){
                        RegionBarrier barrier = RegionService.this.barriers.get(this.uuid);
                        if(barrier != null){
                            barrier.updateGlassBlocks();
                        }

                        if(++this.counter >= 5){
                            this.counter = 0;

                            if(!RegionService.this.plugin.getCombatService().isInCombat(player)){
                                this.cancel();
                                RegionService.this.forceRemove(player);
                            }
                        }
                    } else{
                        this.cancel();
                        RegionService.this.forceRemove(player);
                    }
                }
            }.runTaskTimerAsynchronously(this.plugin, 0, 4);
        }
    }
}
