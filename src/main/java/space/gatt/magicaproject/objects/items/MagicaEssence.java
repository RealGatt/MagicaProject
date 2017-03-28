package space.gatt.magicaproject.objects.items;

import org.bukkit.DyeColor;
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

public class MagicaEssence implements Craftable{

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = MagicaMain.getBaseItemStackable((short)16);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bMana Essence"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Mana Essence";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(new ItemStack(Material.REDSTONE))), getStaticCraftedItem(), 0, 0);
		ItemStack lapis = new ItemStack(Material.INK_SACK, 1, (short) DyeColor.BLUE.getDyeData());
		MagicaRecipe rec2 = new MagicaRecipe(new ArrayList<>(Arrays.asList(lapis)), getStaticCraftedItem(), 0, 0);
		MagicaRecipe rec4 = new MagicaRecipe(new ArrayList<>(Arrays.asList(MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 0, 0, 8);

		recipes.add(rec1);
		recipes.add(rec2);
		recipes.add(rec4);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
