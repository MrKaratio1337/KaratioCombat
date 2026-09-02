package pl.karatiodev.combat.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PluginConfig extends OkaeriConfig {

    @Comment("Combat Settings")
    private Antylogout antylogout = new Antylogout();

    @Getter
    @Setter
    public static class Antylogout extends OkaeriConfig {
        private int time = 30;
        private List<String> start = List.of(
                "<red>You are in combat! Do not log out for <seconds>s.",
                "[ACTIONBAR] <red>Combat: <yellow><seconds>s",
                "[BOSSBAR] <red>Combat: <yellow><seconds>s"
        );
        
        private List<String> end = List.of(
                "<green>You have finished fighting. You can safely log out.\"",
                "[ACTIONBAR] Combat ended!"
        );

        private Regions regions = new Regions();
    }

    @Getter
    @Setter
    public static class Regions extends OkaeriConfig {
        private List<String> blocked = List.of("spawn");
    }

}
