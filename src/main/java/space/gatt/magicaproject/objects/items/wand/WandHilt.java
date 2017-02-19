package space.gatt.magicaproject.objects.items.wand;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.MagicaEssence;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class WandHilt extends Craftable {

	public static ItemStack getBasicHilt() {
		ItemStack manaGenerator = new ItemStack(Material.STICK);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bBasic Hilt"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getBlazeHilt() {
		ItemStack manaGenerator = new ItemStack(Material.BLAZE_ROD);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&cBlazed Hilt"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getBoneHilt() {
		ItemStack manaGenerator = new ItemStack(Material.BONE);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&7Bone Hilt"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Wand Hilt";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.STICK),
						MagicaEssence.getStaticCraftedItem())), getBasicHilt(), 200, 0);

		recipes.add(rec1);

		rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.BLAZE_ROD),
						MagicaShard.getStaticCraftedItem())), getBlazeHilt(), 600, 0);

		recipes.add(rec1);

		rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.BONE),
						MagicaShard.getStaticCraftedItem())), getBoneHilt(), 400, 0);

		recipes.add(rec1);

		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}