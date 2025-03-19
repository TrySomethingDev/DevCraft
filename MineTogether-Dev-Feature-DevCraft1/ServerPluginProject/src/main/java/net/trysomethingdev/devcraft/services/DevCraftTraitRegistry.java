package net.trysomethingdev.devcraft.services;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.reflections.Reflections;

import java.util.Set;
import java.util.logging.Logger;

public class DevCraftTraitRegistry {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String TRAIT_PACKAGE = "net.trysomethingdev.devcraft.traits";

    public void registerTraits() {
        Reflections reflections = new Reflections(TRAIT_PACKAGE);
        Set<Class<? extends Trait>> traitClasses = reflections.getSubTypesOf(Trait.class);

        for (Class<? extends Trait> traitClass : traitClasses) {
            String traitName = traitClass.getSimpleName().toLowerCase();
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(traitClass).withName(traitName));
            log.info("Registered trait: " + traitName);
        }
    }
}
