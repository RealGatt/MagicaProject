package space.gatt.magicaproject.utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonUtils {

	public static HashMap<String, JsonElement> convertToHash(JsonObject array) {
		HashMap<String, JsonElement> map = new HashMap<>();

		Set<Map.Entry<String, JsonElement>> entrySet = array.entrySet();
		for (Map.Entry<String, JsonElement> entry : entrySet) {
			if (!entry.getKey().equalsIgnoreCase("_comment")) {
				map.put(entry.getKey(), entry.getValue());
			}
		}

		return map;
	}

}
