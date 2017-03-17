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

public class WandHilt implements Craftable {

	public static ItemStack getBasicHilt() {
		ItemStack manaGenerator = MagicaMain.getBaseItem((short)4);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bBasic Hilt"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e5%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getBlazeHilt() {
		ItemStack manaGenerator = MagicaMain.getBaseItem((short)6);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&cBlazed Hilt"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e20%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getBoneHilt() {
		ItemStack manaGenerator = MagicaMain.getBaseItem((short)5);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&7Bone Hilt"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e10%"), MagicaMain.getLoreLine().get(0)));
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
				Arrays.asList(
						getBoneHilt(),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						new ItemStack(Material.BLAZE_POWDER),
						MagicaShard.getStaticCraftedItem())), getBlazeHilt(), 650, 0);

		recipes.add(rec1);

		rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(getBasicHilt(),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						new ItemStack(Material.INK_SACK, 0, (byte)15),
						MagicaShard.getStaticCraftedItem())), getBoneHilt(), 450, 0);

		recipes.add(rec1);

		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
