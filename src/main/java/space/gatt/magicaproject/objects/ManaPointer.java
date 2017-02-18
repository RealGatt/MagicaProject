package space.gatt.magicaproject.objects;

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
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManaPointer extends Craftable {

	public static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onItemAdd(EventAddItemToRecipe e) {
				if (e.getCrafter().getItems().size() == getStaticRecipe().size()) {
					if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), getStaticRecipe())) {
						e.getCrafter().beginCrafting(ManaPointer.class, 5, 0, e.getPlayer());
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	public static List<ItemStack> getStaticRecipe() {
		List<ItemStack> items = new ArrayList<>(Arrays.asList(new ItemStack(Material.STICK),
				new ItemStack(Material.GLOWSTONE_DUST)));
		return items;
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

	@Override
	public ItemStack getCraftedItem() {
		return getStaticCraftedItem();
	}

	@Override
	public Material getInventoryMaterial() {
		return Material.REDSTONE_TORCH_ON;
	}

	@Override
	public List<ItemStack> getItemRecipe() {
		return getStaticRecipe();
	}
}
