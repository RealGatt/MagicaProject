package space.gatt.magicaproject.enums;

import org.bukkit.inventory.ItemStack;
import space.gatt.magicaproject.extra.upgrades.CapacityUpgrade;
import space.gatt.magicaproject.extra.upgrades.SpeedUpgrade;
import space.gatt.magicaproject.interfaces.UpgradeInstance;
import space.gatt.magicaproject.objects.items.upgrades.CapacityUpgradeItem;
import space.gatt.magicaproject.objects.items.upgrades.SpeedUpgradeItem;

public enum UpgradeType {
	SPEED_UPGRADE(SpeedUpgrade.class, SpeedUpgradeItem.getStaticCraftedItem()),
	CAPACITY_UPGRADE(CapacityUpgrade.class, CapacityUpgradeItem.getStaticCraftedItem());

	private Class<? extends UpgradeInstance> instance;
	private ItemStack upgradeItem;


	public ItemStack getUpgradeItem() {
		return upgradeItem.clone();
	}

	public UpgradeInstance getNewInstance() {
		try {
			return instance.getConstructor().newInstance();
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	UpgradeType(Class<? extends UpgradeInstance> instance, ItemStack upgradeItem) {

		this.upgradeItem = upgradeItem;
		this.instance = instance;

	}
}
