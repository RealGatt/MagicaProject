package space.gatt.magicaproject.objects.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ManaPointer extends Craftable {

	public static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onItemAdd(EventAddItemToRecipe e) {
				for (MagicaRecipe recipe : getStaticRecipes()){
					if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), recipe.getRequirements())) {
						e.getCrafter().beginCrafting(recipe.getCraftedItem(), recipe.getTimeInTicks(), recipe.getManaPerTick(), e.getPlayer());
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.REDSTONE_TORCH_ON);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bMana Pointer"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Mana Pointer";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(new ItemStack(Material.GLOWSTONE_DUST),
				new ItemStack(Material.STICK))), getStaticCraftedItem(), 5, 0);
		recipes.add(rec1);
		rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.REDSTONE_TORCH_ON),
				new ItemStack(Material.GLOWSTONE_DUST),
				new ItemStack(Material.STICK))), getStaticCraftedItem(), 5, 0, 2);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
