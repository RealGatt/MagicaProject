package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import space.gatt.magicaproject.CancellableBukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.MathUtils;

import java.util.ArrayList;
import java.util.UUID;

public class MagicCrafter extends Craftable implements MagicaBlock, Saveable, Listener {

	private enum STATE{
		CRAFTING, WAITING;
	}

	final int MAX_ITEMS = 16;
	private Location l;
	private boolean isActive;

	private MagicCrafter instance;
	private STATE state = STATE.WAITING;

	private ArrayList<Item> items = new ArrayList<>();

	public MagicCrafter(Location l) {
		this.l = l;
		l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		shutdownCall();
		this.instance = this;
		isActive = true;
	}

	public static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlace(BlockPlaceEvent e) {
				if (e.getBlockPlaced().getType() == Material.ENCHANTMENT_TABLE) {
					ItemStack is = getStaticCraftedItem();
					if (BaseUtils.matchItem(e.getItemInHand(), is)) {
						MagicCrafter mc = new MagicCrafter(e.getBlock().getLocation());
						MagicaMain.getMagicaMain().getBlockManager().registerBlock(mc);
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	BukkitTask craftingTask;

	public boolean beginCrafting(ItemStack resultItem, final float timeInTicks, float manaPerTick, Player p){
		if (state == STATE.WAITING){
			ItemStack is = resultItem;
			state = STATE.CRAFTING;
			Item itemObject = l.getWorld().dropItem(l.clone().add(0.5, 1.5, 0.5), is);
			itemObject.setVelocity(new Vector(0, 0, 0));
			itemObject.setGlowing(true);
			for (Item i : items) {
				i.setGlowing(false);
			}
			itemObject.setPickupDelay(9999999);
			itemObject.setGravity(false);
			itemObject.teleport(l.clone().add(0.5, 1.5, 0.5));
			itemObject.setCustomNameVisible(true);
			l.getWorld().playSound(l, Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 2f);
			if (timeInTicks > 0) {
				itemObject.setCustomName(BaseUtils.colorString("&aCrafting... " + is.getItemMeta().getDisplayName()));
				craftingTask = Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), new CancellableBukkitTask() {
					float timeTaken = 0;
					boolean crafted = false;

					@Override
					public void run() {
						if (crafted == false) {
							timeTaken++;
							itemObject.teleport(l.clone().add(0.5, 1.5, 0.5));
							l.getWorld().spawnParticle(Particle.END_ROD, l.clone().add(0.5, 1.3, 0.5), 10, 0, 0.3, 0, 0);
							itemObject.setCustomName(BaseUtils.colorString("&aCrafting... " +
									is.getItemMeta().getDisplayName() +
									" " + Math.round(timeTaken / timeInTicks * 100) + "%"));
							if (timeTaken >= timeInTicks) {
								for (Item i : items) {
									i.remove();
								}
								items.clear();
								l.getWorld().playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);
								itemObject.setPickupDelay(0);
								itemObject.setCustomName(BaseUtils.colorString("&eCompleted"));
								itemObject.setCustomNameVisible(true);
								state = STATE.WAITING;
								craftingTask.cancel();
								crafted = true;
								return;
							}
						}
					}
				}, 1, 1);
			}else{
				itemObject.teleport(l.clone().add(0.5, 1.5, 0.5));
				l.getWorld().spawnParticle(Particle.END_ROD, l.clone().add(0.5, 1.3, 0.5), 10, 0, 0.3, 0, 0);
				for (Item i : items) {
					i.remove();
				}
				items.clear();
				l.getWorld().playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);
				itemObject.setPickupDelay(0);
				itemObject.setCustomName(BaseUtils.colorString("&eCompleted"));
				itemObject.setCustomNameVisible(true);
				state = STATE.WAITING;
			}
			return true;
		}else{
			return false;
		}
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack magicCrafter = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemMeta im = magicCrafter.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&b&lMana Crafter"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		im.setLore(MagicaMain.getLoreLine());
		magicCrafter.setItemMeta(im);
		return magicCrafter;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive())
		if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) { // Bukkit didn't like checking between two locations
			if (state == STATE.CRAFTING){
				e.setCancelled(true);
				e.getPlayer().sendMessage(BaseUtils.colorString("&cYou must wait until the current item is finished!"));
				return;
			}
			MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
			MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), getStaticCraftedItem());
			isActive = false;
			e.getBlock().getWorld().createExplosion(l.clone().add(0.5, 0.5, 0.5), 0);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (isActive())
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
			e.setUseItemInHand(Event.Result.DENY);
			e.setUseInteractedBlock(Event.Result.DENY);
			if (state == STATE.CRAFTING){
				e.setCancelled(true);
				e.getPlayer().sendMessage(BaseUtils.colorString("&cYou must wait until the current item is finished!"));
				return;
			}
			if (items.size() > 0) {
				for (Item i : items) {
					l.getWorld().dropItemNaturally(e.getClickedBlock().getLocation().add(0.5, 2, 0.5), i.getItemStack());
					i.remove();
				}
				items.clear();
			} else {
				e.getPlayer().sendMessage(BaseUtils.colorString("&bDrop an item to add it to the Magic Crafter"));
			}
		}
	}

	public void runItemSpinner() {
		int id = -1;
		ArrayList<Location> locs = 	MathUtils.getCircle(l.clone().add(0.5, .5, 0.5), 1, 16);

		for (Item as : items) {
			as.setPickupDelay(999999);
			id++;
			as.setVelocity(new Vector(0, 0, 0));
			as.teleport(locs.get(id).clone().add(0, 0.5, 0));
			as.setTicksLived(1);
			as.setFallDistance(0);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (isActive())
		if (items.size() < MAX_ITEMS && state == STATE.WAITING) {
			Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), new CancellableBukkitTask() {
				@Override
				public void run() {
					if (e.getItemDrop().getLocation().getY() < 0) {
						this.cancel();
						return;
					}
					if (items.size() < MAX_ITEMS) {
						if (e.getItemDrop().isOnGround() && e.getItemDrop() != null
								&& !e.getItemDrop().isDead()
								&& !e.getItemDrop().isGlowing()
								&& e.getItemDrop().getCustomName() == null) {
							if (e.getItemDrop().getLocation().getBlockX() == l.getBlockX()
									&& e.getItemDrop().getLocation().distance(l.clone().add(0, 1, 0)) <= 1.5
									&& e.getItemDrop().getLocation().getBlockZ() == l.getBlockZ()) {
								ItemStack i = e.getItemDrop().getItemStack().clone();
								ItemStack copy = e.getItemDrop().getItemStack().clone();
								if (i.getAmount() > 1) {
									i.setAmount(1);
									copy.setAmount(copy.getAmount() - 1);
									e.getItemDrop().setItemStack(i);
									Item i2 = l.getWorld().dropItem(e.getItemDrop().getLocation().getBlock().getLocation().add(0.5, 1, 0.5), copy);
									Bukkit.getPluginManager().callEvent(new PlayerDropItemEvent(e.getPlayer(), i2));
									i2.setVelocity(new Vector(0, 0, 0));
									i2.setPickupDelay(e.getItemDrop().getPickupDelay());
								}
								e.getItemDrop().setCustomName(UUID.randomUUID().toString());
								if (i.getItemMeta().getDisplayName() != null) {
									e.getItemDrop().setCustomName(i.getItemMeta().getDisplayName());
									e.getItemDrop().setCustomNameVisible(true);
								}
								e.getItemDrop().setVelocity(new Vector(0, 0, 0));
								items.add(e.getItemDrop());
								e.getItemDrop().setGravity(false);
								e.getItemDrop().setGlowing(true);
								l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getItemDrop().getLocation(), 5, 0.1, 0.1, 0.1, 0.3);
								e.getItemDrop().setPickupDelay(999999);
								runItemSpinner();
								Bukkit.getPluginManager().callEvent(new EventAddItemToRecipe(instance, i, e.getPlayer()));
								this.cancel();
								return;
							}
						}
					}
				}
			}, 1, 1).getTaskId();
		}
	}

	public ArrayList<ItemStack> getItemsAsStack() {
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item i : getItems()){
			stacks.add(i.getItemStack());
		}
		return stacks;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public int getMAX_ITEMS() {
		return MAX_ITEMS;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String getSaveFileName() {
		return "magiccrafter";
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getFileNameFromLocation(l);
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "class", this.getClass());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", l.getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", l.getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", l.getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", l.getWorld().getName());
	}

	@Override
	public void loadCall(JsonObject loadedObject) {
	}

	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.PORTAL, l.clone().add(0.5, 0.5, 0.5), 10, 0, 0, 0, 1);
		for (Location l : MathUtils.getCircle(l.clone().add(0.5, 1, .5), 1, 64)){
			l.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, l, 1, 0, 0, 0, 0);
		}
		runItemSpinner();
	}

	@Override
	public String getItemName() {
		return "Magica Crafter";
	}

}
