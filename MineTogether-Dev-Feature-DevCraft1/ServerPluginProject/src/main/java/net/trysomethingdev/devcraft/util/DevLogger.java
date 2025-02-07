package net.trysomethingdev.devcraft.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class DevLogger {

    private final static Logger LOGGER = Bukkit.getLogger();

    public static void log(Level level, Object message, Object ...args) {
        LOGGER.log(level, message.toString(), args);
    }

    public static void log(Entity e, Object message, Object ...args) {
        if (e == null) {
            log(Level.WARNING, "Cannot invoke log method without LivingEntity");
            return;    
        }
        var formatted = String.format(message.toString(), args);
        var translated = translaste(formatted);
        e.sendMessage(translated);
    }

    public static void broadcast(Object message, Object ...args) {
        var formatted = String.format(message.toString(), args);
        var translated = translaste(formatted);
        Bukkit.broadcast(translated);
    }

        private static final LegacyComponentSerializer converter =
            LegacyComponentSerializer
                    .builder()
                    .character('&')
                    .hexColors()
                    .build();

    private static Component translaste(String message) {
        return converter.deserialize(message);
    }
}
