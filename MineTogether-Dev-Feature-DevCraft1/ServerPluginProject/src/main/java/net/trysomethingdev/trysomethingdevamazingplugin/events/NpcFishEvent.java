package net.trysomethingdev.trysomethingdevamazingplugin.events;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NpcFishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    private final Item caughtItem;

    public NpcFishEvent(Player player, Item caughtItem) {
        this.player = player;
        this.caughtItem = caughtItem;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getCaughtItem() {
        return caughtItem;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
