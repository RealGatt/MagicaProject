package space.gatt.magicaproject.extra;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MagicaRecipe {

	private ArrayList<ItemStack> items = new ArrayList<>();
	private ItemStack craftedItem;
	private boolean requiresWand = false;
	private int timeInTicks, manaPerTick;

	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, Number timeInTicks) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.timeInTicks = timeInTicks.intValue();
		this.manaPerTick = 0;
	}

	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, Number timeInTicks, int amount) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.craftedItem.setAmount(amount);
		this.timeInTicks = timeInTicks.intValue();
		this.manaPerTick = manaPerTick;
	}
	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, Number timeInTicks, boolean requiresWand) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.requiresWand = requiresWand;
		this.timeInTicks = timeInTicks.intValue();
		this.manaPerTick = 0;
	}

	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, Number timeInTicks, int amount, boolean requiresWand) {
		this.items = items;
		this.requiresWand = requiresWand;
		this.craftedItem = craftedItem;
		this.craftedItem.setAmount(amount);
		this.timeInTicks = timeInTicks.intValue();
		this.manaPerTick = 0;
	}

	@Deprecated
	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}
	@Deprecated
	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick, int amount) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.craftedItem.setAmount(amount);
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}
	@Deprecated
	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick, boolean requiresWand) {
		this.items = items;
		this.craftedItem = craftedItem;
		this.requiresWand = requiresWand;
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}
	@Deprecated
	public MagicaRecipe(ArrayList<ItemStack> items, ItemStack craftedItem, int timeInTicks, int manaPerTick, int amount, boolean requiresWand) {
		this.items = items;
		this.requiresWand = requiresWand;
		this.craftedItem = craftedItem;
		this.craftedItem.setAmount(amount);
		this.timeInTicks = timeInTicks;
		this.manaPerTick = manaPerTick;
	}

	public boolean doesRequireWand() {
		return requiresWand;
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

	@Override
	public String toString(){
		return "MagicaRecipe: {\"" + getCraftedItem().toString() + "\", requiresWand: " + requiresWand + "}";
	}

}
