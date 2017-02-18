package space.gatt.magicaproject;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.managers.BlockManager;
import space.gatt.magicaproject.managers.ManaManager;
import space.gatt.magicaproject.managers.StorageManager;
import space.gatt.magicaproject.objects.blocks.MagicCrafter;

import java.lang.reflect.Method;
import java.util.Set;

public class MagicaMain extends JavaPlugin {

	private static MagicaMain magicaMain;
	private StorageManager storageManager;
	private MagicaMain plugin;
	private ManaManager manaManager;
	private BlockManager blockManager;

	public static MagicaMain getMagicaMain() {
		return magicaMain;
	}

	@Override
	public void onDisable() {
		getManaManager().shutdownCall();
		// StorageManager should be last!!
		storageManager.saveToFile();
	}

	@Override
	public void onEnable() {
		magicaMain = this;
		plugin = this;
		manaManager = new ManaManager();
		blockManager = new BlockManager();
		storageManager = new StorageManager();

		ShapedRecipe magicCrafterRecipe = new ShapedRecipe(MagicCrafter.getStaticCraftedItem());
		magicCrafterRecipe.shape("ABA", "FWR", "IOI");
		magicCrafterRecipe.setIngredient('A', Material.AIR);
		magicCrafterRecipe.setIngredient('B', Material.ENCHANTED_BOOK);
		magicCrafterRecipe.setIngredient('F', Material.YELLOW_FLOWER);
		magicCrafterRecipe.setIngredient('R', Material.RED_ROSE);
		magicCrafterRecipe.setIngredient('I', Material.DIAMOND);
		magicCrafterRecipe.setIngredient('W', Material.WORKBENCH);
		magicCrafterRecipe.setIngredient('O', Material.OBSIDIAN);
		Bukkit.addRecipe(magicCrafterRecipe);

		// Registering Listeners. There's probably a better way but I cbs using reflection rn
		Reflections reflections = new Reflections("space.gatt.magicaproject");

		Set<Class<? extends Craftable>> subTypes = reflections.getSubTypesOf(Craftable.class);
		System.out.println("Found " + subTypes.size() + " Craftable Items.");
		for (Class c : subTypes) {
			try {
				for (Method m : c.getMethods()){
					if (m.getName().equalsIgnoreCase("registerListener")){
						m.invoke(this);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
}
