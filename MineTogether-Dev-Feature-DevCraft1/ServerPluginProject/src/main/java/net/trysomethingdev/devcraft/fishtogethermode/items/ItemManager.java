package net.trysomethingdev.devcraft.fishtogethermode.items;

import net.kyori.adventure.text.Component;
import net.trysomethingdev.devcraft.TrySomethingDevAmazingPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack chest;

    private static TrySomethingDevAmazingPlugin _plugin;

    public static void init(TrySomethingDevAmazingPlugin plugin) {
        _plugin = plugin;
        createFishTogetherModeChest();
    }

    private static void createFishTogetherModeChest(){
        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Fishing Station"));
        List<Component> lore = new ArrayList<>(); lore.add(Component.text("Place this station on the ground to")); lore.add(Component.text("create fishing station"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK,1 ,false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        chest = item;

// create a NamespacedKey for your recipe
        NamespacedKey key = new NamespacedKey(_plugin, "FishTogetherModeChest");

// Create our custom recipe variable
        ShapedRecipe recipe = new ShapedRecipe(key, item);

// Here we will set the places. E and S can represent anything, and the letters can be anything. Beware; this is case sensitive.
        recipe.shape("FFF",
                     "FRF",
                     "FFF");

// Set what the letters represent.
// F = COD, R = FishingRod
        recipe.setIngredient('F', Material.COD);

        recipe.setIngredient('R', Material.FISHING_ROD);

// Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);
    }


}
