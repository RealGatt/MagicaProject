package space.gatt.magicaproject.extra.upgrades;

import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.interfaces.Upgrade;
import space.gatt.magicaproject.interfaces.UpgradeInstance;

public class SpeedUpgrade extends UpgradeInstance implements Upgrade{

	public SpeedUpgrade() {
	}

	@Override
	public int getLevel() {
		return super.getLevel();
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(level);
	}

	@Override
	public void increaseLevel(int level) {
		super.increaseLevel(level);
	}

	@Override
	public void decreaseLevel(int level) {
		super.decreaseLevel(level);
	}

	@Override
	public String upgradeName() {
		return "Speed";
	}

	@Override
	public String upgradeDescription() {
		return "Speeds up the functionality of certain Magica Blocks";
	}

	@Override
	public String upgradeID() {
		return "UPGRADE_SPEED";
	}

	@Override
	public UpgradeType getType() {
		return UpgradeType.SPEED_UPGRADE;
	}
}
