package space.gatt.magicaproject.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class BaseUtils {

	public static String getStringFromLocation(Location l){
		return "X-" + l.getX() + "Y-" + l.getY() + "Z-" + l.getZ() +
				"W-" + l.getWorld() + "Y-" + l.getYaw() + "P-" + l.getPitch();
	}

	public static String colorString(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
