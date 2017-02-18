package space.gatt.magicaproject.interfaces;

import com.google.gson.JsonObject;

public interface Saveable {
	String getSaveFileName();

	String getSaveFileFolder();

	void shutdownCall();

	void loadCall(JsonObject loadedObject);
}
