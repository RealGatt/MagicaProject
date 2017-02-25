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

public class MagicaShard implements Craftable{

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.PRISMARINE_SHARD);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bMana Shard"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Magica Shard";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem(),
				MagicaEssence.getStaticCraftedItem())), getStaticCraftedItem(), 0, 0);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
