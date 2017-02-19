package space.gatt.magicaproject.interfaces;

import com.google.gson.JsonObject;
import org.bukkit.Location;

public interface MagicaBlock {

	Location getLocation();

	void runParticles();

	boolean isActive();

}
