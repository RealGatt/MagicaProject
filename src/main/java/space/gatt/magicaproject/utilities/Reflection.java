package space.gatt.magicaproject.utilities;

/**
 * Created by zacha on 11/6/2016.
 */

import org.bukkit.Bukkit;

public class Reflection {

	public static Class magicaClass(String name){
		try {
			return Class.forName("space.gatt.magicaproject." + name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Class nmsClass(String nms) {
		try {
			return Class.forName("net.minecraft.server." + getServerVersion() + "." + nms);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Class obcClass(String obc) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + obc);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", "@").split("@")[3];
	}
}