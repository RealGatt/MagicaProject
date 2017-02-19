package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;

import java.util.ArrayList;

public class ManaCrucible extends Craftable implements MagicaBlock, Saveable, Listener{

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getSaveFileName() {
		return null;
	}

	@Override
	public void runParticles() {

	}

	@Override
	public String getSaveFileFolder() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public String getItemName() {
		return super.getItemName();
	}

	@Override
	public void shutdownCall() {

	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return super.getRecipes();
	}
}
