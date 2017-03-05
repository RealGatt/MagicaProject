package space.gatt.magicaproject.objects.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class Wrench implements Craftable{

	@Override
	public String getItemName() {
		return "Wrench";
	}

	public static ItemStack getStaticCraftedItem(){
		ItemStack is = MagicaMain.getBaseStack();
		is.setDurability((short)1560);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMagic Wrench"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Allows the ability to rotate Magica Pipes"), MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		is.setItemMeta(im);
		return is;
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.LEVER)
		)), getStaticCraftedItem(), 100, 0, true);
		recipes.add(recipe);
		return recipes;
	}
	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
