package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicaPipe extends MagicaBlock implements Craftable, Saveable, Listener {

	public static void registerListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {



		}, MagicaMain.getMagicaMain());
	}

	public MagicaPipe(Location l) {
		super(l);
		super.setDisplayedInSpawner(false);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	public MagicaPipe(JsonObject object) {
		super(object);
		super.setDisplayedInSpawner(false);

		double x = 0, y = 0, z = 0;
		World world = Bukkit.getWorlds().get(0);
		if (object.has("location-x")){
			x = object.get("location-x").getAsDouble();
		}
		if (object.has("location-y")){
			y = object.get("location-y").getAsDouble();
		}
		if (object.has("location-z")){
			z = object.get("location-z").getAsDouble();
		}
		if (object.has("location-world")){
			world = Bukkit.getWorld(object.get("location-world").getAsString());
		}
		super.setLocation(new Location(world, x, y, z));
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	public static String getStaticSaveFileName(){
		return "magicapipe";
	}


	@Override
	public String getSaveFileName() {
		return getStaticSaveFileName();
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getStringFromLocation(super.getLocation());
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "isActive", super.isActive());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", super.getLocation().getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", super.getLocation().getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", super.getLocation().getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", super.getLocation().getWorld().getName());
	}

	@Override
	public String getItemName() {
		return "Magica Pipe";
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}

	private static ItemStack getStaticCraftedItem(){
		ItemStack is = new ItemStack(Material.END_ROD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&fMagica Pipe"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Allows for the transportation of Magica"),
				BaseUtils.colorString("&7 Can be rotated with the &9Magic Wrench"),
				BaseUtils.colorString("&7 Requires a &9Magica Redirector&7 to change directions."),
				MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		is.setItemMeta(im);
		return is;
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLOWSTONE_DUST)
		)), getStaticCraftedItem(), 1000, 0, 8, true);
		recipes.add(recipe);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
