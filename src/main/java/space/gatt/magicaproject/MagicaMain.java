package space.gatt.magicaproject;

import org.bukkit.plugin.java.JavaPlugin;
import space.gatt.magicaproject.managers.ManaManager;
import space.gatt.magicaproject.managers.StorageManager;

public class MagicaMain extends JavaPlugin {

	private StorageManager storageManager;
	private static MagicaMain magicaMain;

	@Override
	public void onDisable() {
		storageManager.saveToFile();
	}

	@Override
	public void onEnable() {
		magicaMain = this;
		storageManager = new StorageManager();
		new ManaManager();
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public static MagicaMain getMagicaMain() {
		return magicaMain;
	}
}
