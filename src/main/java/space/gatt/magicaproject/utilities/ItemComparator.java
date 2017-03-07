package space.gatt.magicaproject.utilities;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class ItemComparator implements Comparator<ItemStack> {
	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		if (o1.hasItemMeta() && o2.hasItemMeta() && o1.getItemMeta().hasDisplayName() && o2.getItemMeta().hasDisplayName()){
			return ChatColor.stripColor(o1.getItemMeta().getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getItemMeta().getDisplayName()));
		}
		return o1.getType().name().compareTo(o2.getType().name());
	}
}