package pl.karatiodev.combat.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ChatUtility {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component parse(String input){
        if(input == null) return Component.empty();
        if(input.contains("&") || input.contains("§")){
            return LegacyComponentSerializer.legacySection().deserialize(input);
        }

        return MINI_MESSAGE.deserialize(input);
    }
}
