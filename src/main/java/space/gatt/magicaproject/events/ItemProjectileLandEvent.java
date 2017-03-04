package space.gatt.magicaproject.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import space.gatt.magicaproject.extra.ItemProjectile;

public class ItemProjectileLandEvent extends Event{

	private static HandlerList list = new HandlerList();

	private ItemProjectile projectile;
	private Location location;
	private boolean removeEntity;

	public ItemProjectileLandEvent(ItemProjectile projectile, Location location){
		this.projectile = projectile;
		this.location = location;
	}

	public boolean doRemoveEntity() {
		return removeEntity;
	}

	public void removeEntity() {
		this.removeEntity = true;
	}

	public void keepEntity() {
		this.removeEntity = false;
	}

	public ItemProjectile getProjectile() {
		return projectile;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String getEventName() {
		return "Item Projectile Land Event";
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}

	public static HandlerList getHandlerList() {
		return list;
	}

}
