package net.trysomethingdev.devcraft.command;

import net.citizensnpcs.api.npc.NPC;
import net.trysomethingdev.devcraft.util.NpcHelper;
@FunctionalInterface

public interface CommandConsumer<T extends NpcHelper> {
    public void apply(T helper, NPC npc);
}
