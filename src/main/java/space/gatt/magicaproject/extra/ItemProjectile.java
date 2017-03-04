package space.gatt.magicaproject.extra;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.ItemProjectileLandEvent;

public class ItemProjectile {

	private Item entity;
	private ItemStack stack;
	private Location start;
	private BukkitTask task;
	private int aliveTime = 0;

	private String data;

	public ItemProjectile(ItemStack stack, Location start, String data) {
		this.stack = stack;
		this.start = start;
		entity = this.start.getWorld().dropItem(start, stack);
		entity.setCustomName(entity.getUniqueId().toString().substring(0, 8));
		entity.setPickupDelay(100000);
		this.data = data;
	}

	public Item getEntity() {
		return entity;
	}

	public ItemStack getStack() {
		return stack;
	}

	public Location getStart() {
		return start;
	}

	public String getData() {
		return data;
	}

	public ItemProjectile shoot(Vector dir){
		entity.setVelocity(dir);
		task = Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), ()->{
			aliveTime++;
			if (aliveTime >= 5*60*20){
				task.cancel();
				entity.remove();
			}
			if (entity.isOnGround() || entity.isDead() || entity.getFireTicks() > 0){
				ItemProjectileLandEvent event = new ItemProjectileLandEvent(this, entity.getLocation());
				Bukkit.getPluginManager().callEvent(event);
				if (event.doRemoveEntity()) {
					entity.remove();
				}
				task.cancel();
			}
		}, 1, 1);
		return this;
	}

}
