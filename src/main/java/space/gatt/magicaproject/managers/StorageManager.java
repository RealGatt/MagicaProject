package space.gatt.magicaproject.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import space.gatt.magicaproject.MagicaMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class StorageManager {

	private HashMap<Saveable, HashMap<String, Object>> storage = new HashMap<Saveable, HashMap<String, Object>>();

	public boolean save(Saveable saveable, String key, Object val){
		HashMap<String, Object> map = storage.containsKey(saveable) ? storage.get(saveable) : new HashMap<String, Object>();
		map.put(key, val);
		storage.put(saveable, map);
		return storage.containsKey(key);
	}

	public Object load(Saveable saveable, String key){
		return storage.containsKey(saveable) && storage.get(saveable).containsKey(key) ? storage.get(saveable).get(key) : null;
	}

	public void saveToFile(){
		for (Saveable saveable : storage.keySet()){
			saveHash(saveable.getSaveFileName(), storage.get(saveable));
		}
	}

	public void saveHash(String title, HashMap hash){
		File f = new File(MagicaMain.getMagicaMain().getDataFolder() + "/" + title + ".json");
		try {
			if (!MagicaMain.getMagicaMain().getDataFolder().exists()){
				MagicaMain.getMagicaMain().getDataFolder().mkdirs();
			}
			if (!f.exists()){
				f.createNewFile();
			}
		}catch (IOException ignored){
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(hash);

		try {
			FileWriter fileWriter = new FileWriter(f);
			fileWriter.write(json);
			fileWriter.close();
		}catch (IOException fileE){
			fileE.printStackTrace();
		}

		System.out.println(json);
	}

}
