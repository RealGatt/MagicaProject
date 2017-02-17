package space.gatt.magicaproject;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import space.gatt.magicaproject.managers.BlockManager;
import space.gatt.magicaproject.managers.ManaManager;
import space.gatt.magicaproject.managers.StorageManager;
import space.gatt.magicaproject.utilities.BaseUtils;

public class MagicaMain extends JavaPlugin {

	private StorageManager storageManager = new StorageManager();
	private static MagicaMain magicaMain;
	private ManaManager manaManager = new ManaManager();
	private BlockManager blockManager = new BlockManager();

	@Override
	public void onDisable() {
		storageManager.saveToFile();
	}

	@Override
	public void onEnable() {
		magicaMain = this;

		ItemStack magicCrafter = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemMeta im = magicCrafter.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&b&lMana Crafter"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		magicCrafter.setItemMeta(im);

		ShapelessRecipe magicCrafterRecipe = new ShapelessRecipe(magicCrafter);
		magicCrafterRecipe.addIngredient(2, Material.DIAMOND);
		magicCrafterRecipe.addIngredient(1, Material.ENCHANTED_BOOK);
		magicCrafterRecipe.addIngredient(1, Material.RED_ROSE);
		magicCrafterRecipe.addIngredient(1, Material.YELLOW_FLOWER);
		magicCrafterRecipe.addIngredient(1, Material.WORKBENCH);
		Bukkit.addRecipe(magicCrafterRecipe);

	}

	public BlockManager getBlockManager() {
		return blockManager;
	}

	public ManaManager getManaManager() {
		return manaManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public static MagicaMain getMagicaMain() {
		return magicaMain;
	}
}
