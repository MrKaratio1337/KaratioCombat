package pl.karatiodev.combat.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PluginConfig extends OkaeriConfig {

    @Comment("Combat Settings")
    private Antylogout antylogout = new Antylogout();

    @Comment("Messages Settings")
    private Messages messages = new Messages();

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

    @Getter
    @Setter
    public static class Messages extends OkaeriConfig {
        @CustomKey("logouyt-during-fight")
        private String logoutDuringFight = "<red>Player <yellow><player></yellow> logged out during combat!";

        @CustomKey("cannot-enter-region")
        private String cannotEnterRegion = "<red>You cannot enter this region during combat!";
    }
}
