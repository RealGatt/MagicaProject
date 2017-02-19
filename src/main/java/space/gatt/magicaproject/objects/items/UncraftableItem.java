package space.gatt.magicaproject.objects.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class UncraftableItem extends Craftable {

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.DIRT);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&c&lDirtiest of Dirt"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		im.setLore(MagicaMain.getLoreLine());
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Uncraftable";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK),
				new ItemStack(Material.BEDROCK))), getStaticCraftedItem(), 10000000, 0);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
