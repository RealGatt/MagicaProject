package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.EntityBlock;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.wand.Wand;
import space.gatt.magicaproject.objects.items.wand.WandCore;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicWorkbench extends Craftable implements EntityBlock, MagicaBlock, Saveable{


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
		for (MagicaRecipe wand : Wand.getStaticRecipes()){
			MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
					wand.getCraftedItem(),
					new ItemStack(Material.WORKBENCH),
					WandCore.getWoodCore()
			)), getStaticCraftedItem(), (6000-wand.getTimeInTicks())/2, 0);
			recipes.add(recipe);
		}

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
