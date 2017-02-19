package space.gatt.magicaproject.utilities;

import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InventoryWorkAround {

	public static boolean canMerge(final Inventory inventory1, final Inventory inventory2) {
		if ((inventory1.firstEmpty() < 0)) {
			if (!hasContents(inventory1, inventory2)) {
				return false;
			}

			for (ItemStack stack1 : getItems(inventory1)) {
				for (ItemStack stack2 : getItems(inventory2)) {
					if ((stack1.isSimilar(stack2)) && (stack1.getAmount() + stack2.getAmount() > stack1.getMaxStackSize())) {
						return false;
					}
				}
			}
		}

		if (getEmptySlots(inventory1) < getFilledSlots(inventory2)) {
			for (ItemStack stack1 : getItems(inventory1)) {
				for (ItemStack stack2 : getItems(inventory2)) {
					if ((stack1.isSimilar(stack2)) && (stack1.getAmount() + stack2.getAmount() > stack1.getMaxStackSize())) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public static boolean canAdd(final Inventory inventory1, final ItemStack item) {
		if ((inventory1.firstEmpty() < 0)) {
			for (ItemStack stack1 : getItems(inventory1)) {
				if (BaseUtils.matchItem(stack1, item) && (stack1.getAmount() + item.getAmount() <= stack1.getMaxStackSize())){
					return true;
				}
				if (BaseUtils.matchItem(stack1, item) && (stack1.getAmount() + item.getAmount() > stack1.getMaxStackSize())) {
					return false;
				}
			}
		}

		if (getEmptySlots(inventory1) < 1) {
			for (ItemStack stack1 : getItems(inventory1)) {
				if (BaseUtils.matchItem(stack1, item) && (stack1.getAmount() + item.getAmount() <= stack1.getMaxStackSize())){
					return true;
				}
				if (BaseUtils.matchItem(stack1, item) && (stack1.getAmount() + item.getAmount() > item.getMaxStackSize())) {
					return false;
				}
			}
		}
		return true;
	}

	public static int getEmptySlots(final Inventory inventory) {
		int amount = 0;
		for (ItemStack stack : inventory.getContents()) {
			if (stack == null) {
				amount++;
			}
		}
		return amount;
	}

	public static int getFilledSlots(final Inventory inventory) {
		int amount = 0;
		for (ItemStack stack : inventory.getContents()) {
			if (stack != null) {
				amount++;
			}
		}
		return amount;
	}

	public static boolean hasContents(final Inventory inventory1, final Inventory inventory2) {
		for (ItemStack stack : getItems(inventory2)) {
			if(!inventory1.contains(stack.getType())) {
				return false;
			}
		}
		return true;
	}

	public static List<ItemStack> getItems(final Inventory inventory) {
		List<ItemStack> contents = new ArrayList<ItemStack>();
		for (ItemStack stack : inventory.getContents()) {
			if (stack != null) {
				contents.add(stack);
			}
		}

		return contents;
	}

}