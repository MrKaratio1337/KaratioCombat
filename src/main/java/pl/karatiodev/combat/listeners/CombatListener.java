package pl.karatiodev.combat.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.utilities.ChatUtility;
import pl.karatiodev.combat.utilities.RegionUtility;

import java.util.Locale;

@RequiredArgsConstructor
public class CombatListener implements Listener {

    private final CombatPlugin plugin;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if(event.isCancelled()) return;

        if(!(event.getEntity() instanceof Player target)) return;

        Player attacker = this.getAttacker(event.getDamager());
        if(attacker == null && event.getDamager() instanceof Mob && plugin.getPluginConfig().getAntylogout().getSettings().isMobs()){
            if(!RegionUtility.isBlockedRegion(target.getLocation(), plugin)){
                this.plugin.getCombatService().startCombat(target);
            }
            return;
        }

        if(attacker != null){
            if(attacker.getGameMode() == GameMode.CREATIVE) return;

            boolean targetInBlockedRegion = RegionUtility.isBlockedRegion(target.getLocation(), plugin);
            boolean attackerInBlockedRegion = RegionUtility.isBlockedRegion(attacker.getLocation(), plugin);

            if(targetInBlockedRegion && !attackerInBlockedRegion){
                event.setCancelled(true);
                return;
            }

            if(!targetInBlockedRegion && !attackerInBlockedRegion){
                this.plugin.getCombatService().startCombat(target);
                this.plugin.getCombatService().startCombat(attacker);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if(cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL && cause != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) return;

        Player player = event.getPlayer();
        if(player.hasPermission("karatiocombat.bypass")) return;

        Location to = event.getTo();
        if(to == null || !plugin.getCombatService().isInCombat(player)) return;

        if(RegionUtility.isBlockedRegion(to, plugin)){
            event.setCancelled(true);
            player.sendMessage(ChatUtility.parse(plugin.getPluginConfig().getMessages().getCannotEnterRegion()));
        } else{
            plugin.getRegionService().forceRemove(player);
            if(plugin.getCombatService().isInCombat(player)){
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getServer().getPluginManager().callEvent(new PlayerMoveEvent(player, event.getFrom(), to));
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("karatiocombat.bypass")) return;

        if(plugin.getCombatService().isInCombat(player) && !plugin.getPluginConfig().getAntylogout().getSettings().isEnderchest() && event.getClickedBlock() != null
        && event.getClickedBlock().getType() == Material.ENDER_CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK){
            event.setCancelled(true);
            player.sendMessage(ChatUtility.parse(plugin.getPluginConfig().getMessages().getCannotOpenEnderchest()));
            return;
        }

        if(plugin.getCombatService().isInCombat(player) && event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)){
            if(plugin.getPluginConfig().getAntylogout().getSettings().isPearl()){
                plugin.getCombatService().startCombat(player);
            }

            Block targetBlock = player.getTargetBlockExact(5);
            if(targetBlock != null && RegionUtility.isBlockedRegion(targetBlock.getLocation(), plugin)){
                cancelPearl(event, player);
                return;
            }

            Location currentLocation = player.getLocation();
            Vector direction = currentLocation.getDirection();

            for(int i = 0; i < 16; i++){
                currentLocation.add(direction);
                if(RegionUtility.isBlockedRegion(currentLocation, plugin)){
                    cancelPearl(event, player);
                    return;
                }
            }
        }
    }

    private void cancelPearl(PlayerInteractEvent event, Player player) {
        event.setCancelled(true);
        player.sendMessage(ChatUtility.parse(this.plugin.getPluginConfig().getMessages().getCannotEnterRegion()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player dead = event.getEntity();

        if(this.plugin.getCombatService().isInCombat(dead)) this.plugin.getCombatService().endCombat(dead);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(this.plugin.getCombatService().isInCombat(player)) this.plugin.getCombatService().handleQuit(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("karatiocombat.bypass")) return;

        if(this.plugin.getCombatService().isInCombat(player)){
            String command = event.getMessage().substring(1).split(" ")[0].toLowerCase(Locale.ROOT);

            boolean isWhitelisted = plugin.getPluginConfig().getAntylogout().getCommands().getWhitelist().contains(command);
            if(!isWhitelisted){
                event.setCancelled(true);
                player.sendMessage(ChatUtility.parse(this.plugin.getPluginConfig().getMessages().getCannotUseCommand()));
            }
        }
    }

    private Player getAttacker(Entity damager){
        if(damager instanceof Player player) return player;

        if(damager instanceof Projectile projectile && plugin.getPluginConfig().getAntylogout().getSettings().isProjectile()){
            if(projectile instanceof EnderPearl && !plugin.getPluginConfig().getAntylogout().getSettings().isPearl()) return null;

            if(projectile.getShooter() instanceof Player shooter){
                if(shooter.getGameMode() == GameMode.CREATIVE) return null;

                return shooter;
            }
        }

        return null;
    }
}
