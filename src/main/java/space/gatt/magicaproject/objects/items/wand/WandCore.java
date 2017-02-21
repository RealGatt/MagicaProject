package space.gatt.magicaproject.objects.items.wand;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class WandCore extends Craftable{

	public static ItemStack getPureMagicaCore() {
		ItemStack manaGenerator = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&b&oPure Magica Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e90%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getObsidianCore() {
		ItemStack manaGenerator = new ItemStack(Material.COAL);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&5Obsidian Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e50%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getDiamondCore() {
		ItemStack manaGenerator = new ItemStack(Material.DIAMOND);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&9Diamond Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e45%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getGoldCore() {
		ItemStack manaGenerator = new ItemStack(Material.GOLD_INGOT);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&eGold Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e30%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getIronCore() {
		ItemStack manaGenerator = new ItemStack(Material.IRON_INGOT);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&fIron Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e20%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getStoneCore() {
		ItemStack manaGenerator = new ItemStack(Material.CLAY_BALL);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&7Stone Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e10%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	public static ItemStack getWoodCore() {
		ItemStack manaGenerator = new ItemStack(Material.BOWL);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&8Wood Core"));
		im.addItemFlags(ItemFlag.values());
		//im.setLore(Arrays.asList(BaseUtils.colorString("&9 Crafting Time Reduction: &e5%"), MagicaMain.getLoreLine().get(0)));
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}



	@Override
	public String getItemName() {
		return "Wand Core";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();

		MagicaRecipe pureMagicaCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(getDiamondCore(),
						getGoldCore(),
						getIronCore(),
						getStoneCore(),
						getWoodCore(),
						getObsidianCore(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem())), getPureMagicaCore(), 10000, 0);

		recipes.add(pureMagicaCore);

		MagicaRecipe obsidianCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.OBSIDIAN),
						new ItemStack(Material.OBSIDIAN),
						new ItemStack(Material.OBSIDIAN),
						new ItemStack(Material.OBSIDIAN),
						new ItemStack(Material.OBSIDIAN),
						MagicaShard.getStaticCraftedItem())), getObsidianCore(), 1400, 0);

		recipes.add(obsidianCore);

		MagicaRecipe diamondCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.DIAMOND),
						new ItemStack(Material.DIAMOND),
						new ItemStack(Material.DIAMOND),
						MagicaShard.getStaticCraftedItem())), getDiamondCore(), 800, 0);
		recipes.add(diamondCore);

		MagicaRecipe goldCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.GOLD_INGOT),
						new ItemStack(Material.GOLD_INGOT),
						MagicaShard.getStaticCraftedItem())), getGoldCore(), 400, 0);

		recipes.add(goldCore);

		MagicaRecipe ironCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.IRON_INGOT),
						MagicaShard.getStaticCraftedItem())), getIronCore(), 600, 0);

		recipes.add(ironCore);

		MagicaRecipe stoneCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.STONE),
						MagicaShard.getStaticCraftedItem())), getStoneCore(), 60, 0);

		recipes.add(stoneCore);

		MagicaRecipe woodCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.LOG),
						MagicaShard.getStaticCraftedItem())), getWoodCore(), 20, 0);

		recipes.add(woodCore);
		woodCore = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.LOG_2),
						MagicaShard.getStaticCraftedItem())), getWoodCore(), 20, 0);

		recipes.add(woodCore);

		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

}
