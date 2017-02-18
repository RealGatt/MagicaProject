package space.gatt.magicaproject.interfaces;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Craftable {

	public String getItemName(){
		return "MagicaProjectCraftable";
	}

	public ItemStack getCraftedItem(){
		return new ItemStack(Material.AIR);
	}

	public Material getInventoryMaterial(){
		return Material.AIR;
	}

	public List<ItemStack> getItemRecipe(){
		return new ArrayList<>(Arrays.asList(new ItemStack(Material.AIR)));
	}
}
