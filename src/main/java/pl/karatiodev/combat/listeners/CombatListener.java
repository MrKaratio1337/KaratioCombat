package pl.karatiodev.combat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

        }
    }
}
