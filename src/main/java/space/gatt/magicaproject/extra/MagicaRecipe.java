package space.gatt.magicaproject.extra;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MagicaRecipe {

	private ArrayList<ItemStack> items = new ArrayList<>();
	private ItemStack craftedItem;
	private int timeInTicks, manaPerTick;

	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}

	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick, int amount) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.craftedItem.setAmount(amount);
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}

	public int getTimeInTicks() {
		return timeInTicks;
	}

	public int getManaPerTick() {
		return manaPerTick;
	}

	public ArrayList<ItemStack> getRequirements() {
		return items;
	}

	public ItemStack getCraftedItem() {
		return craftedItem;
	}
}
