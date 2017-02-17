package space.gatt.magicaproject.managers;

import space.gatt.magicaproject.MagicaMain;

public class ManaManager implements Saveable{

	public String getSaveFileName() {
		return "mana";
	}

	public ManaManager() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "wow", 5312);
		MagicaMain.getMagicaMain().getStorageManager().save(this, "wowString", "test String");
		MagicaMain.getMagicaMain().getStorageManager().save(this, "Crazy", this);
		MagicaMain.getMagicaMain().getStorageManager().saveToFile();
	}
}
