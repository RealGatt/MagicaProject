package space.gatt.magicaproject.objects.items.wand;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wand extends Craftable{

	@Override
	public String getItemName() {
		return "Wand";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();

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

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
