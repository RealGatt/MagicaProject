package space.gatt.magicaproject.interfaces;

import com.google.gson.JsonObject;

public interface Saveable {
	public String getSaveFileName();
	public void shutdownCall();
	public void loadCall(JsonObject loadedObject);
}
