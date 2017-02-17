package space.gatt.magicaproject.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
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

	public boolean removeFromSave(Saveable saveable){
		if (storage.containsKey(saveable)){
			storage.remove(saveable);
		}
		return true;
	}

	public Object load(Saveable saveable, String key){
		return storage.containsKey(saveable) && storage.get(saveable).containsKey(key) ? storage.get(saveable).get(key) : null;
	}

	public void saveToFile(){
		for (Saveable saveable : storage.keySet()){
			saveable.shutdownCall();
			if (storage.get(saveable) != null && !storage.get(saveable).isEmpty()) {
				if (saveable instanceof MagicaBlock){
					if (((MagicaBlock)saveable).isActive()){
						saveHash(saveable.getSaveFileFolder(), saveable.getSaveFileName(), storage.get(saveable));
					}
				}else{
					saveHash(saveable.getSaveFileFolder(), saveable.getSaveFileName(), storage.get(saveable));
				}

			}
		}
	}

	public void saveHash(String path, String title, HashMap hash){
		File f = new File(MagicaMain.getMagicaMain().getDataFolder() + "/" + path + "/" + title + ".json");
		File pathF = new File(MagicaMain.getMagicaMain().getDataFolder() + "/" + path);
		boolean fileexists = false;
		try {
			if (!MagicaMain.getMagicaMain().getDataFolder().exists()){
				MagicaMain.getMagicaMain().getDataFolder().mkdirs();
			}
			if (!pathF.exists()){
				pathF.mkdirs();
			}
			if (!f.exists()){
				fileexists = f.createNewFile();
			}
		}catch (IOException ignored){
		}
		if (fileexists) {
			Gson gson = new Gson();
			System.out.println("Preparing to create JSON Magic for " + title + ".");
			String json = gson.toJson(hash);
			try {
				FileWriter fileWriter = new FileWriter(f);
				fileWriter.write(json);
				fileWriter.close();
			} catch (IOException fileE) {
			}
		}else{
			System.out.println("The file didn't get created! Aborting!");
		}
	}

}
