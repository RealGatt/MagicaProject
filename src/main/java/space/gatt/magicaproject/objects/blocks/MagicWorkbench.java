package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.wand.WandCore;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicWorkbench extends MagicaBlock implements Craftable, Saveable, Listener{
	private Location l;

	public MagicWorkbench(Location l) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		this.l = l;
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	public MagicWorkbench(JsonObject object){
		super(object);

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
		this.l = new Location(world, x, y, z);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);

	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive())
			if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				super.setActive(false);
			}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (isActive())
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				e.setCancelled(true);
				InventoryView view = e.getPlayer().openWorkbench(e.getClickedBlock().getLocation(), true);
				ItemStack workBenchHoe = MagicaMain.getBaseGUI();
				workBenchHoe.setDurability((short)2);
				CraftingInventory inv = ((CraftingInventory)view.getTopInventory());
				inv.setResult(workBenchHoe);
			}
	}

	public static ItemStack getStaticCraftedItem() {

		ItemStack bench = MagicaMain.getBaseBlockStack();
		bench.setDurability((short)5);
		ItemMeta im = bench.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&eWorkbench of Magica"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Allows for the searching of recipes"),
				BaseUtils.colorString("&7 for both Magica items and regular"),
				BaseUtils.colorString("&7 Minecraft items."), MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		bench.setItemMeta(im);
		return bench;
	}

	@Override
	public String getItemName() {
		return "Magica Workbench";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.WORKBENCH),
				WandCore.getWoodCore()
		)), getStaticCraftedItem(), 500, 0, true);
		recipes.add(recipe);

		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}


	public static String getStaticSaveFileName(){
		return "magicworkbench";
	}

	@Override
	public String getSaveFileName() {
		return getStaticSaveFileName();
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getStringFromLocation(l);
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "isActive", super.isActive());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", l.getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", l.getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", l.getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", l.getWorld().getName());
	}

	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {

	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}

	@Override
	public boolean isActive() {
		return super.isActive();
	}
}
