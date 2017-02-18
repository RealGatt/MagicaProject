package space.gatt.magicaproject.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.JsonUtils;

import java.util.HashMap;
import java.util.UUID;

public class ManaManager implements Saveable {

	HashMap<UUID, Float> manaStorage = new HashMap<UUID, Float>();
	HashMap<UUID, Float> maxManaStorage = new HashMap<UUID, Float>();

	public ManaManager() {
	}

	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "MaxMana", maxManaStorage);
		MagicaMain.getMagicaMain().getStorageManager().save(this, "Mana", manaStorage);
	}

	public void loadCall(JsonObject loadedObjects) {
		if (loadedObjects.has("Mana")) {
			HashMap<String, JsonElement> manaHashConverted = JsonUtils.convertToHash(loadedObjects.get("Mana").getAsJsonObject());
			for (String s : manaHashConverted.keySet()) {
				UUID u = UUID.fromString(s);
				manaStorage.put(u, manaHashConverted.get(s).getAsFloat());
			}
		}
		if (loadedObjects.has("MaxMana")) {
			HashMap<String, JsonElement> manaHashConverted = JsonUtils.convertToHash(loadedObjects.get("MaxMana").getAsJsonObject());
			for (String s : manaHashConverted.keySet()) {
				UUID u = UUID.fromString(s);
				maxManaStorage.put(u, manaHashConverted.get(s).getAsFloat());
			}
		}
	}

	@Override
	public String getSaveFileFolder() {
		return "/";
	}

	public String getSaveFileName() {
		return "mana";
	}
}
