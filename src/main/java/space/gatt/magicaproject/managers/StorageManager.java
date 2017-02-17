package space.gatt.magicaproject.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.Saveable;

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
			saveable.shutdownCall();
			if (storage.get(saveable) != null && !storage.get(saveable).isEmpty()) {
				saveHash(saveable.getSaveFileName(), storage.get(saveable));
			}
		}
	}

	public void saveHash(String title, HashMap hash){
		File f = new File(MagicaMain.getMagicaMain().getDataFolder() + "/" + title + ".json");
		System.out.println("Creating paths");
		try {
			File path = new File(f.getPath());
			if (!path.exists()){
				path.mkdirs();
			}
			if (!f.exists()){
				f.createNewFile();
			}
		}catch (IOException ignored){
			ignored.printStackTrace();
		}
		System.out.println("Created paths");
		Gson gson = new Gson();
		String json = gson.toJson(hash);
		System.out.println("JSON: " + json);

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
