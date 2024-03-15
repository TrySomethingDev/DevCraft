package net.trysomethingdev.devcraft.util;

import io.papermc.paper.math.BlockPosition;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.util.AbstractBlockBreaker;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DevBlockBreaker extends AbstractBlockBreaker {
    private final NPC npc;

    public DevBlockBreaker(NPC npc, org.bukkit.block.Block target,
                                BlockBreakerConfiguration config) {
            super(npc.getEntity(), target, config);
         this.npc = npc;

        }

        private ItemStack getCurrentItem() {
            var eqTrait = npc.getOrAddTrait(Equipment.class);
            List<ItemStack> eq = List.of(eqTrait.getEquipment());
            ItemStack mainHand = eq.get(0);
            return mainHand;
        }

        @Override
        protected float getDamage(int tickDifference) {
            return getStrength(npc.getEntity().getWorld().getBlockAt(x, y, z)) * (tickDifference + 1)
                    * configuration.blockStrengthModifier();
        }

        private Entity getHandle() {
            return npc.getEntity();
        }

        private float getStrength(Block block) {
        return 1f;
//            float base = block.get(null, new BlockPosition(0, 0, 0));
//            return base < 0.0F ? 0.0F : !isDestroyable(block) ? 1.0F / base / 100.0F : strengthMod(block) / base / 30.0F;
        }

//        private boolean isDestroyable(Block block) {
//            if (block.getType().isBlock()..isAlwaysDestroyable())
//                return true;
//            else {
//                ItemStack current = getCurrentItem();
//                return current != null ? current.(block) : false;
//            }
//        }

        @Override
        protected void setBlockDamage(int modifiedDamage) {

                getHandle().getWorld().spawnParticle(Particle.BLOCK_CRACK,getHandle().getWorld().getBlockAt(x,y,z).getLocation(), modifiedDamage);

        }




        private float strengthMod(Block block) {

        return 1;

//            ItemStack itemstack = getCurrentItem();
//            float f = itemstack.a(block);
//            if (getHandle() instanceof EntityLiving) {
//                EntityLiving handle = (EntityLiving) getHandle();
//                if (f > 1.0F) {
//                    int i = EnchantmentManager.getDigSpeedEnchantmentLevel(handle);
//                    if (i > 0) {
//                        f += i * i + 1;
//                    }
//                }
//                if (handle.hasEffect(MobEffectList.FASTER_DIG)) {
//                    f *= 1.0F + (handle.getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1) * 0.2F;
//                }
//                if (handle.hasEffect(MobEffectList.SLOWER_DIG)) {
//                    float f1 = 1.0F;
//                    switch (handle.getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) {
//                        case 0:
//                            f1 = 0.3F;
//                            break;
//                        case 1:
//                            f1 = 0.09F;
//                            break;
//                        case 2:
//                            f1 = 0.0027F;
//                            break;
//                        case 3:
//                        default:
//                            f1 = 8.1E-4F;
//                    }
//                    f *= f1;
//                }
//                if (handle.a(Material.WATER) && !EnchantmentManager.j(handle)) {
//                    f /= 5.0F;
//                }
//            }
//            if (!getHandle().onGround) {
//                f /= 5.0F;
//            }
//            return f;
        }
    }

