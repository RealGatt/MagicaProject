package space.gatt.magicaproject.extra.upgrades;

import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.interfaces.Upgrade;
import space.gatt.magicaproject.interfaces.UpgradeInstance;

public class CapacityUpgrade extends UpgradeInstance implements Upgrade{

	public CapacityUpgrade() {
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
		return "Capacity";
	}

	@Override
	public String upgradeDescription() {
		return "Enables certain Magica Blocks to store more of something";
	}

	@Override
	public String upgradeID() {
		return "UPGRADE_CAPACITY";
	}

	@Override
	public UpgradeType getType() {
		return UpgradeType.CAPACITY_UPGRADE;
	}
}
