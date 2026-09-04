package pl.karatiodev.combat.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.karatiodev.combat.CombatPlugin;
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

        return null;
    }
}
