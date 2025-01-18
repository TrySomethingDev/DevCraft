//package net.trysomethingdev.devcraft.traits;
//
//import net.citizensnpcs.api.trait.Trait;
//import net.citizensnpcs.api.trait.TraitName;
//
//@TraitName("basetrait")
//public abstract class BaseTrait extends Trait {
//
//    protected BaseTrait(String name) {
//        super(name);
//    }
//
//    // No-args constructor for reflection
//    protected BaseTrait() {
//        super("basetrait");
//    }
//    protected void log(String message) {
//        getNPC().getEntity().getServer().getLogger().info("[Trait: " + getName() + "] " + message);
//    }
//}