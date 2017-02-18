package space.gatt.magicaproject.utilities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class MathUtils {

	public static ArrayList<Location> getCircle(final Location center, final double radius, final int amount) {
		final World world = center.getWorld();
		final double increment = (Math.PI * 2) / amount;
		final ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; ++i) {
			final double angle = i * increment;
			final double x = center.getX() + radius * Math.cos(angle);
			final double z = center.getZ() + radius * Math.sin(angle);
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

	public static ArrayList<Location> getStar(final Location center, final double radius, final int amount) {
		final World world = center.getWorld();
		final ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 1; i < 6; i++) {
			Vector v = new Vector(Math.cos(i * (72 * Math.PI / 180)) * radius, 0, Math.sin(i * (72 * Math.PI / 180)) * radius);
			Vector star = v.clone();
			double rx, rz, rcos, rsin;
			rcos = Math.cos((144 * Math.PI / 180));
			rsin = Math.sin((144 * Math.PI / 180));
			rx = star.getX() * rcos + star.getZ() * rsin;
			rz = star.getX() * -rsin + star.getZ() * rcos;
			star.setX(rx).setZ(rz);
			center.add(v);
			Vector link = star.clone().subtract(v.clone());
			float length = (float) link.length();
			link.normalize();
			float ratio = length / amount;
			Vector v3 = link.multiply(ratio);
			Location loc = center.clone().subtract(v3);
			locations.add(new Location(world, loc.add(v3).getX(), center.getY(), loc.add(v3).getZ()));
			center.subtract(v);
		}
		return locations;
	}

}
