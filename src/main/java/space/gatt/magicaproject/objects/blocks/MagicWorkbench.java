package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.EntityBlock;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.wand.WandCore;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicWorkbench extends MagicaBlock implements EntityBlock, Craftable, Saveable{
	private Location l;

	public MagicWorkbench(Location l) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		this.l = l;
		shutdownCall();
	}

	public MagicWorkbench(JsonObject object){
		super(object);

	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack bench = new ItemStack(Material.WORKBENCH);
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


	@Override
	public void spawnExtra() {

	}

	@Override
	public void destroyExtra() {

	}

	@Override
	public String getSaveFileName() {
		return null;
	}

	@Override
	public String getSaveFileFolder() {
		return null;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public void runParticles() {

	}

	@Override
	public void shutdownCall() {

	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}

	@Override
	public boolean isActive() {
		return false;
	}
}
