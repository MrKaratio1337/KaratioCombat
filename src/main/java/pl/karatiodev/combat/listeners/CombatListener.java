package pl.karatiodev.combat.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.karatiodev.combat.CombatPlugin;

public class CombatListener implements Listener {

    private final CombatPlugin plugin;

    public CombatListener(CombatPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if(event.isCancelled()) return;

        if(event.getEntity() instanceof Player target){
            Player attacker = this.getAttacker(event.getDamager());

            if(attacker != null){
                if(attacker.getGameMode() == GameMode.CREATIVE) return;

                this.plugin.getCombatService().startCombat(target);
                this.plugin.getCombatService().startCombat(attacker);
            }
        }
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
