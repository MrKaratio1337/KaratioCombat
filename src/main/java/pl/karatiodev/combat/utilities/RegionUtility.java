package pl.karatiodev.combat.utilities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import pl.karatiodev.combat.CombatPlugin;

public final class RegionUtility {

    public static boolean isBlockedRegion(Location location, CombatPlugin plugin){
        if(location == null || location.getWorld() == null) return false;
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.getApplicableRegions(weLocation).getRegions().stream().anyMatch(region -> plugin.getPluginConfig().getAntylogout().getRegions().getBlocked().contains(region.getId()));
    }
}
