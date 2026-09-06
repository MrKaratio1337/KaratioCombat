package pl.karatiodev.combat.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import pl.karatiodev.combat.CombatPlugin;
import pl.karatiodev.combat.utilities.ChatUtility;

@Command(name = "karatiocombat")
@Permission("karatiocombat.admin")
@RequiredArgsConstructor
public class KaratioCombatCommand {

    private final CombatPlugin plugin;

    @Execute(name = "reload")
    public void executeReload(@Context CommandSender sender){
        this.plugin.getPluginConfig().load();
        this.plugin.getMessageService().clearAllBossBars();
        sender.sendMessage(ChatUtility.parse(this.plugin.getPluginConfig().getMessages().getConfigReloaded()));
    }
}
