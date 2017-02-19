package space.gatt.magicaproject.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class StorageManager {

	private HashMap<Saveable, HashMap<String, Object>> storage = new HashMap<Saveable, HashMap<String, Object>>();


	ArrayList<File> scannedfiles = new ArrayList<>();

	public StorageManager() {
		File f = new File(MagicaMain.getMagicaMain().getDataFolder() + "/");
		listf(f, scannedfiles);
		Bukkit.getLogger().info("Scanned saved files and found " + scannedfiles.size() + " files.");
	}

	public boolean save(Saveable saveable, String key, Object val) {
		HashMap<String, Object> map = storage.containsKey(saveable) ? storage.get(saveable) : new HashMap<String, Object>();
		map.put(key, val);
		storage.put(saveable, map);
		return storage.containsKey(key);
	}

	public boolean registerSaveable(Saveable saveable){
		if (!storage.containsKey(saveable)){
			storage.put(saveable, new HashMap<>());
		}
		return true;
	}

	public boolean removeFromSave(Saveable saveable) {
		if (storage.containsKey(saveable)) {
			storage.remove(saveable);
		}
		return true;
	}

	public Object load(Saveable saveable, String key) {
		return storage.containsKey(saveable) && storage.get(saveable).containsKey(key) ? storage.get(saveable).get(key) : null;
	}

	public void listf(File directory, ArrayList<File> files) {
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file, files);
			}
		}
	}

	public void loadFromFile(String key, Class classType){
		for (File f : scannedfiles){
			try {
				FileReader reader = new FileReader(f);
				BufferedReader br = new BufferedReader(reader);
				StringJoiner builder = new StringJoiner("\n");
				br.lines().forEachOrdered(new Consumer<String>() {
					@Override
					public void accept(String s) {
						builder.add(s);
					}
				});
				JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();
				if (jsonObject.has("Key")){
					if (jsonObject.get("Key").getAsString().equalsIgnoreCase(key)){
						if (jsonObject.has("isActive") && jsonObject.get("isActive").getAsBoolean()) {
							classType.asSubclass(Saveable.class).getConstructor(JsonObject.class).newInstance(jsonObject);
						}
					}
				}
				br.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		for (File f : scannedfiles){
			Bukkit.getLogger().info(f.delete() ? "Deleted file " + f.getName() + "!":"Could not delete file " + f.getName());
		}
		for (Saveable saveable : storage.keySet()) {
			Bukkit.getLogger().info("Saving data for an " + saveable.getSaveFileName() + " instance.");
			saveable.shutdownCall();
			if (storage.get(saveable) != null && !storage.get(saveable).isEmpty()) {
				saveHash(saveable.getSaveFileFolder(), saveable.getSaveFileName(), storage.get(saveable));
			}
		}
	}

	public void saveHash(String path, String title, HashMap hash) {
		File f = new File(MagicaMain.getMagicaMain().getDataFolder() + "/" + path + ".json");
		hash.put("Key", title);
		boolean fileexists = false;
		try {
			if (!MagicaMain.getMagicaMain().getDataFolder().exists()) {
				MagicaMain.getMagicaMain().getDataFolder().mkdirs();
			}
			if (!f.exists()) {
				Bukkit.getLogger().info("File doesn't exist!");
				fileexists = f.createNewFile();
			}else{
				f.delete();
				fileexists = f.createNewFile();
			}
		} catch (IOException ignored) {
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
		} else {
			System.out.println("The file didn't get created! Aborting!");
		}
	}

}
