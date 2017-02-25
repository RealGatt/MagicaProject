package space.gatt.magicaproject.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BaseUtils {

	public static boolean isSameListItems(ArrayList<ItemStack> stack1, ArrayList<ItemStack> stack2){
		if (stack1.size() != stack2.size()){
			return false;
		}
		int trueCount = 0;
		ArrayList<ItemStack> stack1Clone = new ArrayList<>();
		for (ItemStack i1 : stack1){
			stack1Clone.add(i1);
		}
		ArrayList<ItemStack> stack2Clone = new ArrayList<>();
		for (ItemStack i1 : stack2){
			stack2Clone.add(i1);
		}
		for (ItemStack is1 : stack1Clone){
			boolean foundClone = false;
			int id = -1;
			for (ItemStack is2 : stack2Clone){
				id++;
				foundClone = matchItem(is1, is2);
				if (foundClone){
					stack2Clone.remove(id);
					trueCount++;
					break;
				}

			}
		}
		return trueCount == stack1.size();

	}

	public static String getStringFromLocation(Location l) {
		return "-" + l.getX() +
				"-" + l.getY() +
				"-" + l.getZ() +
				"-" + l.getWorld().getName();
	}

	public static String getFileNameFromLocation(Location l) {
		return "X" + l.getX() + "/Y" + l.getY() + "/Z" + l.getZ() +
				"/W" + l.getWorld().getName();
	}

	public static boolean matchItem(ItemStack i1, ItemStack i2) {
		if (i1 != null && i2 != null) {
			i1 = i1.clone();
			i2 = i2.clone();
			i1.setItemMeta(i1.getItemMeta());
			i2.setItemMeta(i2.getItemMeta());
			ItemMeta im1 = i1.getItemMeta();
			ItemMeta im2 = i2.getItemMeta();
			boolean nameMatch = true, loreMatch = true, enchantMatch = true, materialMatch;
			materialMatch = (i1.getType() == i2.getType());
			if (im1.getDisplayName() != null && im2.getDisplayName() != null) {
				nameMatch = im1.getDisplayName().equals(im2.getDisplayName());
			} else {
				if (im1.getDisplayName() != null && im2.getDisplayName() == null) {
					nameMatch = false;
				} else if (im2.getDisplayName() != null && im1.getDisplayName() == null) {
					nameMatch = false;
				}
			}
			if ((im1.getLore() != null && !im1.getLore().isEmpty()) && (im2.getLore() != null && !im2.getLore().isEmpty())) {
				loreMatch = im1.getLore().containsAll(im2.getLore());
			} else {
				if (im1.getLore() != null && im2.getLore() == null) {
					loreMatch = false;
				} else if (im2.getLore() != null && im1.getLore() == null) {
					loreMatch = false;
				}
			}

			if (!im1.getEnchants().isEmpty() && !im2.getEnchants().isEmpty()) {
				enchantMatch = im1.getEnchants().equals(im2.getEnchants());
			} else {
				if (im1.getEnchants() != null && im2.getEnchants() == null) {
					enchantMatch = false;
				} else if (im2.getEnchants() != null && im1.getEnchants() == null) {
					enchantMatch = false;
				}
			}
			return nameMatch && loreMatch && enchantMatch && materialMatch;
		}else{
			return false;
		}

	}

	public static String colorString(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
