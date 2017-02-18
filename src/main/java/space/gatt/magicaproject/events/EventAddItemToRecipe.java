package space.gatt.magicaproject.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import space.gatt.magicaproject.objects.MagicCrafter;

public class EventAddItemToRecipe extends Event {

	private MagicCrafter crafter;
	private ItemStack item;
	private Player p;

	public EventAddItemToRecipe(MagicCrafter crafter, ItemStack item, Player p) {
		this.crafter = crafter;
		this.item = item;
		this.p = p;
	}

	public MagicCrafter getCrafter() {
		return crafter;
	}

	public ItemStack getItem() {
		return item;
	}

	public Player getPlayer() {
		return p;
	}

	@Override
	public String getEventName() {
		return super.getEventName();
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
