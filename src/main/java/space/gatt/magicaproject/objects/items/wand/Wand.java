package space.gatt.magicaproject.objects.items.wand;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wand extends Craftable{

	@Override
	public String getItemName() {
		return "Wand";
	}

	private static ArrayList<ItemStack> wands = new ArrayList<>();

	private static ArrayList<MagicaRecipe> recipes = new ArrayList<>();;

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		for (MagicaRecipe hilt : WandHilt.getStaticRecipes()){
			for (MagicaRecipe core : WandCore.getStaticRecipes()){

				ItemStack wand = new ItemStack(hilt.getCraftedItem().getType());
				ItemMeta im = wand.getItemMeta();
				String[] coreType = core.getCraftedItem().getItemMeta().getDisplayName().split(" ");
				String name = ChatColor.stripColor(StringUtils.join(coreType, " ", 0, coreType.length - 1));
				String hiltType = ChatColor.stripColor(hilt.getCraftedItem().getItemMeta().getDisplayName().split(" ")[0]);
				im.setDisplayName(BaseUtils.colorString("&6" + hiltType + " Wand of " + name));
				List<String> lore = new ArrayList<String>();
				lore.add(BaseUtils.colorString(" &8Hilt: " + hilt.getCraftedItem().getItemMeta().getDisplayName()));
				lore.add(BaseUtils.colorString(" &8Core: " + core.getCraftedItem().getItemMeta().getDisplayName()));
				lore.addAll(MagicaMain.getLoreLine());
				im.setLore(lore);
				wand.setItemMeta(im);
				wands.add(wand);
				MagicaRecipe newWand = new MagicaRecipe(
						new ArrayList<>(Arrays.asList(
						hilt.getCraftedItem(),
						core.getCraftedItem(),
						MagicaShard.getStaticCraftedItem())), wand, ((hilt.getTimeInTicks() + core.getTimeInTicks()) / 2),
						0);
				recipes.add(newWand);
			}
		}
		return recipes;
	}

	public static boolean isItemWand(Item i){
		return isItemWand(i.getItemStack());
	}

	public static boolean isItemWand(ItemStack i){
		for (ItemStack wand : wands){
			if (BaseUtils.matchItem(wand, i)){
				return true;
			}
		}
		return false;
	}

	public static MagicaRecipe getRecipeForWand(ItemStack wand){
		for (MagicaRecipe mr : recipes){
			if (BaseUtils.matchItem(mr.getCraftedItem(), wand)){
				return mr;
			}
		}
		return null;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
