package pl.karatiodev.combat.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.region.RegionBarrier;
import pl.karatiodev.combat.utilities.RegionUtility;

@RequiredArgsConstructor
public class RegionListener implements Listener {

    private final CombatPlugin plugin;

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(this.plugin.getCombatService().isInCombat(player)){
            Location to = event.getTo();
            Location from = event.getFrom();
            if (to.getX() != from.getX() || to.getZ() != from.getZ() || to.getY() != from.getY()) {
                World weWorld = BukkitAdapter.adapt(to.getWorld());
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);

                if(regionManager == null){
                    this.plugin.getRegionService().remove(player);
                } else{
                    boolean inBlockedRegion = RegionUtility.isBlockedRegion(to, this.plugin);
                    if(inBlockedRegion){
                        event.setCancelled(true);
                        this.plugin.getRegionService().send(player);
                        this.plugin.getRegionService().remove(player);
                    } else{
                        updateBarrier(player, to);
                    }
                }
            }
        } else{
            this.plugin.getRegionService().remove(player);
        }
    }

    private void updateBarrier(Player player, Location location){
        World weWorld = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);

        if(regionManager == null){
            this.plugin.getRegionService().remove(player);
        } else{
            ProtectedRegion closestRegion = null;
            double closestDistance = Double.MAX_VALUE;
            BlockVector3 closestEdgePoint = null;

            for(ProtectedRegion region : regionManager.getRegions().values()){
                if(this.plugin.getPluginConfig().getAntylogout().getRegions().getBlocked().contains(region.getId())){
                    BlockVector3 min = region.getMinimumPoint();
                    BlockVector3 max = region.getMaximumPoint();

                    double closestX = Math.max(min.x(), Math.min(location.getX(), max.x()));
                    double closestZ = Math.max(min.z(), Math.min(location.getZ(), max.z()));

                    Location pointOnRegion = new Location(location.getWorld(), closestX, location.getY(), closestZ);
                    double distance = location.distance(pointOnRegion);

                    if(distance < closestDistance && distance <= 5.0){
                        closestDistance = distance;
                        closestRegion = region;
                        closestEdgePoint = BlockVector3.at(closestX, location.getY(), closestZ);
                    }
                }
            }

            if(closestRegion != null && closestEdgePoint != null){
                Location barrierLocation = new Location(location.getWorld(), closestEdgePoint.x(), location.getY(), closestEdgePoint.z());
                RegionBarrier barrier = new RegionBarrier(player, barrierLocation, closestRegion, closestDistance);
                this.plugin.getRegionService().show(player, barrier);
            } else{
                this.plugin.getRegionService().remove(player);
            }
        }
    }
}
