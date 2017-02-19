package space.gatt.magicaproject;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.managers.BlockManager;
import space.gatt.magicaproject.managers.ManaManager;
import space.gatt.magicaproject.managers.RecipeManager;
import space.gatt.magicaproject.managers.StorageManager;
import space.gatt.magicaproject.objects.blocks.MagicCrafter;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MagicaMain extends JavaPlugin implements Listener{

	private static MagicaMain magicaMain;
	private StorageManager storageManager;
	private MagicaMain plugin;
	private ManaManager manaManager;
	private BlockManager blockManager;
	private RecipeManager recipeManager;

	public static MagicaMain getMagicaMain() {
		return magicaMain;
	}

	@Override
	public void onDisable() {
		getManaManager().shutdownCall();
		getBlockManager().shutdown();
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
		recipeManager = new RecipeManager();

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

		Bukkit.getPluginManager().registerEvents(this, this);

		Reflections reflections = new Reflections("space.gatt.magicaproject");

		Set<Class<? extends Craftable>> subTypes = reflections.getSubTypesOf(Craftable.class);
		System.out.println("Found " + subTypes.size() + " Craftable Items.");
		for (Class c : subTypes) {
			try {
				for (Method m : c.getMethods()){
					if (m.getReturnType() == ArrayList.class && Modifier.isStatic(m.getModifiers())){
						ArrayList<MagicaRecipe> recipes = (ArrayList<MagicaRecipe>)m.invoke(this);
						for (MagicaRecipe recipe : recipes){
							recipeManager.registerRecipe(recipe);
						}
						System.out.println("Registered recipes for " + c.getSimpleName());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Set<Class<? extends MagicaBlock>> magicaBlocks = reflections.getSubTypesOf(MagicaBlock.class);
		System.out.println("Found " + magicaBlocks.size() + " MagicaBlocks.");
		for (Class c : magicaBlocks) {
			try {
				for (Method m : c.getMethods()){
					if (m.getName().equalsIgnoreCase("registerListener")){
						m.invoke(this);
						System.out.println("Registered listeners for " + c.getSimpleName());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getLoreLine(){
		return Arrays.asList(BaseUtils.colorString("&9MagicaProject"));
	}


	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e){
		if (e.getEntity().getCustomName() != null){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onCraft(CraftItemEvent e){
		for (ItemStack is : e.getInventory().getMatrix()){
			ItemMeta im = is.getItemMeta();
			if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
				e.setCancelled(true);
				e.setResult(Event.Result.DENY);
				e.setCurrentItem(new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler
	public void onFurnace(FurnaceBurnEvent e){
		ItemMeta im = e.getFuel().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
			e.setBurning(false);
			e.setBurnTime(0);
		}
	}

	@EventHandler
	public void onFurnace(FurnaceSmeltEvent e){
		ItemMeta im = e.getSource().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
		}
		im = e.getResult().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onAnvil(final PrepareAnvilEvent e){
		e.getInventory().forEach(new Consumer<ItemStack>() {
			@Override
			public void accept(ItemStack stack) {
				if (stack != null && stack.hasItemMeta()) {
					ItemMeta im = stack.getItemMeta();
					if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))) {
						e.setResult(new ItemStack(Material.AIR));
					}
				}
			}
		});
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
