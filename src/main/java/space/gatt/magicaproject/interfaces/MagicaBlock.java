package space.gatt.magicaproject.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface MagicaBlock {

	public Location getLocation();
	public void runParticles();
	public String getBlockName();
	public ItemStack getCraftedItem();
	public Material getInventoryMaterial();
	public List<ItemStack> getItemRecipe();
	public boolean isActive();

}
