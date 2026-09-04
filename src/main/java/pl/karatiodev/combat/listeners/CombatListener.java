package pl.karatiodev.combat.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.utilities.ChatUtility;
import pl.karatiodev.combat.utilities.RegionUtility;

@RequiredArgsConstructor
public class CombatListener implements Listener {

    private final CombatPlugin plugin;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if(event.isCancelled()) return;

        if(event.getEntity() instanceof Player target){
            Player attacker = this.getAttacker(event.getDamager());

            if(attacker == null && event.getDamager() instanceof Mob && this.plugin.getPluginConfig().getAntylogout().getSettings().isMobs()){
                if(!RegionUtility.isBlockedRegion(target.getLocation(), this.plugin)){
                    this.plugin.getCombatService().startCombat(target);
                }
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
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL || event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT){
            Player player = event.getPlayer();

            if(player.hasPermission("antylogout.bypass")) return;

            Location to = event.getTo();
            Location from = event.getFrom();

            if(to != null && this.plugin.getCombatService().isInCombat(player)){
                if(RegionUtility.isBlockedRegion(to, this.plugin)){
                    event.setCancelled(true);
                    player.sendMessage(ChatUtility.parse(this.plugin.getPluginConfig().getMessages().getCannotEnterRegion()));
                } else{
                    this.plugin.getRegionService().forceRemove(player);
                    if(this.plugin.getCombatService().isInCombat(player)){
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                            this.plugin.getServer().getPluginManager().callEvent(new PlayerMoveEvent(player, from, to));
                        }, 1L);
                    }
                }
            }
        }
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

    private Player getAttacker(Entity damager){
        if(damager instanceof Player player) return player;
        if(damager instanceof Projectile projectile && this.plugin.getPluginConfig().getAntylogout().getSettings().isProjectile()){
            if(projectile instanceof EnderPearl && !this.plugin.getPluginConfig().getAntylogout().getSettings().isPearl()){
                return null;
            }
        }

        return null;
    }
}
