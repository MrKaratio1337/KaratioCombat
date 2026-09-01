package pl.karatiodev.combat.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PluginConfig extends OkaeriConfig {

    @Comment("Ustawianie Antylogoutu")
    private Antylogout antylogout = new Antylogout();

    @Getter
    @Setter
    public static class Antylogout extends OkaeriConfig {
        private Regions regions = new Regions();
    }

    @Getter
    @Setter
    public static class Regions extends OkaeriConfig {
        private List<String> blocked = List.of("spawn");
    }

}
