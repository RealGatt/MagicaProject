package space.gatt.magicaproject.interfaces;

import space.gatt.magicaproject.enums.UpgradeType;

public class UpgradeInstance {

	private int level = 0;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void increaseLevel(int level) {
		this.level += level;
	}

	public void decreaseLevel(int level) {
		this.level -= level;
	}

	public UpgradeType getType(){
		return null;
	}
}
