package net.trysomethingdev.trysomethingdevamazingplugin.minetogethermode.items;

import net.kyori.adventure.text.Component;
import net.trysomethingdev.trysomethingdevamazingplugin.TrySomethingDevAmazingPlugin;
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
        createMineTogetherModeChest();



    }

    private static void createMineTogetherModeChest(){
        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Mine Together Mode Chest"));
        List<String> lore22 = new ArrayList<>();

        List<Component> lore = new ArrayList<>(); lore.add(Component.text("Place this chest on the ground to")); lore.add(Component.text("start Mine Together Mode"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK,1 ,false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        

        item.setItemMeta(meta);
        chest = item;


// create a NamespacedKey for your recipe
        NamespacedKey key = new NamespacedKey(_plugin, "MineTogetherModeChest");

// Create our custom recipe variable
        ShapedRecipe recipe = new ShapedRecipe(key, item);

// Here we will set the places. E and S can represent anything, and the letters can be anything. Beware; this is case sensitive.
        recipe.shape("GGG",
                     "GCG",
                     "GGG");


// Set what the letters represent.
// E = Emerald, S = Stick
        recipe.setIngredient('G', Material.GOLD_INGOT);

        recipe.setIngredient('C', Material.CHEST);

// Finally, add the recipe to the bukkit recipes
        Bukkit.addRecipe(recipe);

    }


}
