package pl.karatiodev.combat.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public class RegionBarrier {

    private final Player player;
    private Location center;
    private final ProtectedRegion region;
    private double distance;
    private final Set<Location> glassBlocks = new HashSet<>();
    private Set<Location> previousGlassBlocks = new HashSet<>();

    public RegionBarrier(Player player, Location center, ProtectedRegion region, double distance) {
        this.player = player;
        this.center = center.clone();
        this.region = region;
        this.distance = distance;
        updateGlassBlocks();
        show();
    }

    public void update(Location newCenter, ProtectedRegion region, double distance){
        this.center = newCenter.clone();
        this.distance = distance;
        updateGlassBlocks();
    }

    public void updateGlassBlocks(){
        Set<Location> oldBlocks = new HashSet<>(this.glassBlocks);
        this.glassBlocks.clear();

        BlockVector3 min = this.region.getMinimumPoint();
        BlockVector3 max = this.region.getMaximumPoint();

        int minX = min.x();
        int maxX = max.x();
        int minZ = min.z();
        int maxZ = max.z();

        int yLevel = this.center.getBlockY();
        int centerX = this.center.getBlockX();
        int centerZ = this.center.getBlockZ();

        int width = Math.max(1, (int) Math.ceil(1.0 + (5.0 - this.distance) * 2.0));
        int halfWidth = width / 2;

        int startX = Math.max(minX, centerX - halfWidth);
        int endX = Math.min(maxX, centerX + halfWidth);
        int startZ = Math.max(minZ, centerZ - halfWidth);
        int endZ = Math.min(maxZ, centerZ + halfWidth);

        if (startZ == minZ) addGlassBlocks(startX, endX, startZ, yLevel);
        if (endZ == maxZ) addGlassBlocks(startX, endX, endZ, yLevel);
        if (startX == minX) addGlassBlocksZ(startZ, endZ, startX, yLevel, minZ, maxZ);
        if (endX == maxX) addGlassBlocksZ(startZ, endZ, endX, yLevel, minZ, maxZ);

        for(Location location : oldBlocks){
            if(!this.glassBlocks.contains(location)){
                this.player.sendBlockChange(location, location.getBlock().getBlockData());
            }
        }

        for (Location location : this.glassBlocks) {
            this.player.sendBlockChange(location, Material.RED_STAINED_GLASS.createBlockData());
        }

        this.previousGlassBlocks = new HashSet<>(this.glassBlocks);
    }

    private void addGlassBlocks(int startX, int endX, int z, int yLevel){
        for(int x = startX; x <= endX; x++){
            for(int y = 0; y <= 5; y++){
                Location location = new Location(this.center.getWorld(), x, yLevel + y, z);
                if(y != 0 || location.getBlock().getType() == Material.AIR){
                    this.glassBlocks.add(location);
                }
            }
        }
    }

    private void addGlassBlocksZ(int startZ, int endZ, int x, int yLevel, int minZ, int maxZ){
        for(int z = startZ; z <= endZ; z++){
            for(int y = 0; y <= 5; y++){
                Location location = new Location(this.center.getWorld(), x, yLevel + y, z);
                if ((y != 0 || location.getBlock().getType() == Material.AIR) && (z != startZ || startZ != minZ) && (z != endZ || endZ != maxZ)) {
                    this.glassBlocks.add(location);
                }
            }
        }
    }

    public void show(){
        for(Location location : this.glassBlocks){
            this.player.sendBlockChange(location, Material.RED_STAINED_GLASS.createBlockData());
        }

        this.previousGlassBlocks = new HashSet<>(this.glassBlocks);
    }

    public void hide(){
        for(Location location : this.previousGlassBlocks){
            this.player.sendBlockChange(location, location.getBlock().getBlockData());
        }

        for(Location location : this.glassBlocks){
            this.player.sendBlockChange(location, location.getBlock().getBlockData());
        }

        this.previousGlassBlocks.clear();
        this.glassBlocks.clear();
    }
}
