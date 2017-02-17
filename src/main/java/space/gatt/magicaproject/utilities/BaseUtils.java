package space.gatt.magicaproject.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BaseUtils {

	public static String getStringFromLocation(Location l){
		return "X-" + l.getX() + "Y-" + l.getY() + "Z-" + l.getZ() +
				"W-" + l.getWorld().getName() + "Y-" + l.getYaw() + "P-" + l.getPitch();
	}
	public static String getFileNameFromLocation(Location l){
		return "X" + l.getX() + "/Y" + l.getY() + "/Z" + l.getZ() +
				"/W" + l.getWorld().getName();
	}

	public static boolean matchItem(ItemStack i1, ItemStack i2){
		ItemMeta im1 = i1.getItemMeta();
		ItemMeta im2 = i2.getItemMeta();
		boolean nameMatch = true, loreMatch = true, enchantMatch = true;
		if (im1.getDisplayName() != null && im2.getDisplayName() != null){
			nameMatch = im1.getDisplayName().equals(im2.getDisplayName());
		}else{
			if (im1.getDisplayName() != null && im2.getDisplayName() == null){
				nameMatch = false;
			}else if (im2.getDisplayName() != null && im1.getDisplayName() == null){
				nameMatch = false;
			}
		}
		if ((im1.getLore() != null && !im1.getLore().isEmpty()) && (im2.getLore() != null && !im2.getLore().isEmpty())){
			loreMatch = im1.getLore().containsAll(im2.getLore());
		}else{
			if (im1.getLore() != null && im2.getLore() == null){
				loreMatch = false;
			}else if (im2.getLore() != null && im1.getLore() == null){
				loreMatch = false;
			}
		}
		if (!im1.getEnchants().isEmpty() && !im2.getEnchants().isEmpty()){
			enchantMatch = im1.getEnchants().equals(im2.getEnchants());
		}
		else{
			if (im1.getEnchants() != null && im2.getEnchants() == null){
				enchantMatch = false;
			}else if (im2.getEnchants() != null && im1.getEnchants() == null){
				enchantMatch = false;
			}
		}
		return nameMatch && loreMatch && enchantMatch;

	}

	public static String colorString(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
