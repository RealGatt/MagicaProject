package space.gatt.magicaproject.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface MagicaBlock {

	Location getLocation();

	void runParticles();

	boolean isActive();

}
