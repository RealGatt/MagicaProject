package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Hopper;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import space.gatt.magicaproject.CancellableBukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.wand.Wand;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.InventoryWorkAround;
import space.gatt.magicaproject.utilities.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MagicCrafter extends MagicaBlock implements Saveable, Listener {

	private enum STATE{
		CRAFTING, WAITING
	}

	final int MAX_ITEMS = 16;
	private Location l;

	private MagicCrafter instance;
	private STATE state = STATE.WAITING;

	private Item craftingItemObject = null;
	private Item wand = null; // TODO
	private ArrayList<Item> items = new ArrayList<>();

	private ArrayList<Location> spinnerLocations;
	private ArrayList<Location> wandSpinnerLocations;

	public boolean hasWand(){
		return wand != null;
	}

	public Item getWand() {
		return wand;
	}

	public MagicCrafter(Location l) {
		super(l);
		this.l = l;
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		this.instance = this;
		runItemSpinner();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	public MagicCrafter(JsonObject object){
		super(object);

		double x = 0, y = 0, z = 0;
		World world = Bukkit.getWorlds().get(0);
		if (object.has("location-x")){
			x = object.get("location-x").getAsDouble();
		}
		if (object.has("location-y")){
			y = object.get("location-y").getAsDouble();
		}
		if (object.has("location-z")){
			z = object.get("location-z").getAsDouble();
		}
		if (object.has("location-world")){
			world = Bukkit.getWorld(object.get("location-world").getAsString());
		}
		this.l = new Location(world, x, y, z);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		this.instance = this;
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		runItemSpinner();
	}
	BukkitTask craftingTask;

	public boolean beginCrafting(ItemStack resultItem, final float timeInTicks, float manaPerTick, Player p){
		if (state == STATE.WAITING){
			ItemStack is = resultItem;
			state = STATE.CRAFTING;
			craftingItemObject = l.getWorld().dropItem(l.clone().add(0.5, 1.5, 0.5), is);
			craftingItemObject.setVelocity(new Vector(0, 0, 0));

			craftingItemObject.setPickupDelay(9999999);
			craftingItemObject.setGravity(false);
			craftingItemObject.teleport(l.clone().add(0.5, 1.5, 0.5));
			craftingItemObject.setCustomNameVisible(true);
			for (Item i : items) {
				i.setCustomNameVisible(false);
				i.setInvulnerable(true);
			}
			l.getWorld().playSound(l, Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 2f);
			if (timeInTicks > 0) {
				craftingItemObject.setCustomName(BaseUtils.colorString("&aCrafting... " + is.getItemMeta().getDisplayName()));
				craftingTask = Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), new CancellableBukkitTask() {
					float timeTaken = 0;
					boolean crafted = false;
					float previousPerc = 0;
					Random rnd = new Random();
					@Override
					public void run() {
						if (crafted == false) {
							timeTaken++;
							craftingItemObject.teleport(l.clone().add(0.5, 1.5, 0.5));
							l.getWorld().spawnParticle(Particle.END_ROD, l.clone().add(0.5, 1.3, 0.5), 10, 0, 0.1, 0, 0);
							craftingItemObject.setCustomName(BaseUtils.colorString("&aCrafting... " +
									is.getItemMeta().getDisplayName() +
									" &a" + Math.round(timeTaken / timeInTicks * 100) + "%"));
							if (Math.round(timeTaken / timeInTicks * 100) > previousPerc) {
								for (Item i : items) {
									i.setGlowing(rnd.nextBoolean());
								}
								previousPerc = Math.round(timeTaken / timeInTicks * 100);
							}
							if (timeTaken >= timeInTicks) {
								Item finishedItem = craftingItemObject;
								craftingItemObject = null;
								for (Item i : items) {
									i.remove();
								}
								items.clear();
								l.getWorld().playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);
								finishedItem.setPickupDelay(0);
								finishedItem.setCustomName(BaseUtils.colorString("&eCompleted"));
								finishedItem.setCustomNameVisible(true);
								state = STATE.WAITING;
								craftingTask.cancel();
								crafted = true;
								placeInHopper(finishedItem);
								return;
							}
						}
					}
				}, 1, 1);
			}else{
				Item finishedItem = craftingItemObject;
				craftingItemObject = null;
				finishedItem.teleport(l.clone().add(0.5, 1.5, 0.5));
				l.getWorld().spawnParticle(Particle.END_ROD, l.clone().add(0.5, 1.3, 0.5), 10, 0, 0.3, 0, 0);
				for (Item i : items) {
					i.remove();
				}
				items.clear();
				l.getWorld().playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);
				finishedItem.setPickupDelay(0);
				finishedItem.setCustomName(BaseUtils.colorString("&eCompleted"));
				finishedItem.setCustomNameVisible(true);
				state = STATE.WAITING;
				placeInHopper(finishedItem);
			}
			return true;
		}else{
			return false;
		}
	}

	Random random = new Random();

	private void placeInHopper(Item i){
		Hopper hp = null;
		List<Hopper> hoppers = new ArrayList<>();
		if (getLocation().subtract(0, 1, 0).getBlock().getType() == Material.HOPPER){
			hp = (Hopper)getLocation().subtract(0, 1, 0).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().add(1, 0, 0).getBlock().getType() == Material.HOPPER){ // Up
			hp = (Hopper)getLocation().add(1, 0, 0).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().add(0, 0, 1).getBlock().getType() == Material.HOPPER){ // Right
			hp = (Hopper)getLocation().add(0, 0, 1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().add(1, 0, 1).getBlock().getType() == Material.HOPPER){ // Up + Left
			hp = (Hopper)getLocation().add(1, 0, 1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().add(1, 0, -1).getBlock().getType() == Material.HOPPER){ // Up + Right
			hp = (Hopper)getLocation().add(1, 0, -1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().subtract(1, 0, 0).getBlock().getType() == Material.HOPPER){ // Down
			hp = (Hopper)getLocation().subtract(1, 0, 0).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().subtract(0, 0, 1).getBlock().getType() == Material.HOPPER){ // Left
			hp = (Hopper)getLocation().subtract(0, 0, 1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().subtract(1, 0, 1).getBlock().getType() == Material.HOPPER){ // Down + Right
			hp = (Hopper)getLocation().subtract(1, 0, 1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (getLocation().subtract(1, 0, -1).getBlock().getType() == Material.HOPPER){ // Down + Left
			hp = (Hopper)getLocation().subtract(1, 0, -1).getBlock().getState();
			if (InventoryWorkAround.canAdd(hp.getInventory(), i.getItemStack())){
				hoppers.add(hp);
			}
		}
		if (hoppers.size() > 0) {
			placeInHopper(hoppers.get(random.nextInt(hoppers.size())), i);
		}
	}

	private void placeInHopper(Hopper hp, Item i){

		i.teleport(hp.getLocation().add(0, 0.5, 0));
		hp.getLocation().getWorld().playEffect(hp.getLocation(), Effect.STEP_SOUND, new MaterialData(Material.HOPPER).getItemTypeId());
		hp.getInventory().addItem(i.getItemStack());
		i.remove();
		i.getWorld().playSound(i.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1);

	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack magicCrafter = MagicaMain.getBaseStack();
		magicCrafter.setDurability((short)1);
		ItemMeta im = magicCrafter.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&b&lMagica Crafter"));
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
			e.setCancelled(true);
			if (items.size() > 0) {
				for (Item i : items) {
					l.getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 2, 0.5), i.getItemStack());
					i.remove();
				}
				items.clear();
			}
			if (hasWand()){
				l.getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 2, 0.5), getWand().getItemStack());
				getWand().remove();
			}
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), getStaticCraftedItem());
			super.setActive(false);
			e.getBlock().getWorld().createExplosion(l.clone().add(0.5, 0.5, 0.5), 0);
			spinnerTask.cancel();
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
			if (items.size() > 0 || hasWand()) {
				for (Item i : items) {
					l.getWorld().dropItemNaturally(e.getClickedBlock().getLocation().add(0.5, 2, 0.5), i.getItemStack());
					i.remove();
				}
				items.clear();
				if (hasWand()){
					l.getWorld().dropItemNaturally(e.getClickedBlock().getLocation().add(0.5, 2, 0.5), getWand().getItemStack());
					getWand().remove();
				}
				wand = null;
			} else {
				e.getPlayer().sendMessage(BaseUtils.colorString("&bDrop an item ontop of the Magic Crafter to add it"));
			}
		}
	}
	private int wandSpinnerLocation = 0;

	private BukkitTask spinnerTask;
	public void runItemSpinner() {
		this.wandSpinnerLocations = MathUtils.getCircle(l.clone().add(0.5, .5, 0.5), 0.7, 128);
		spinnerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MagicaMain.getMagicaMain(), ()->{
			spinnerLocations = MathUtils.getCircle(l.clone().add(0.5, .5, 0.5), 1, items.size());
			wandSpinnerLocation++;
			if (wandSpinnerLocation == wandSpinnerLocations.size() - 1){
				wandSpinnerLocation = 0;
			}
			if (hasWand()){
				getWand().teleport(wandSpinnerLocations.get(wandSpinnerLocation).clone().add(0, 0.5, 0));
				getWand().setPickupDelay(99999);
				getWand().setTicksLived(1);
				getWand().setGravity(false);
			}
			int id = -1;
			boolean isstar = false;
			ArrayList<Location> locs = !isstar ? spinnerLocations :
					MathUtils.getStar(l.clone().add(0.5, .5, 0.5), 1, items.size(), 1);
			Location mid = l.clone().add(0.5, 0.5, 0.5);
			for (Item as : items) {
				as.setPickupDelay(99999);
				id++;
				as.setVelocity(new Vector(0, 0, 0));
				as.teleport(locs.get(id).clone().add(0, 0.5, 0));
				as.setTicksLived(1);
				as.setFallDistance(0);
				if (state == STATE.CRAFTING) {
					Location point = as.getLocation().clone().subtract(0, 1.3, 0);
					Vector dir = new Vector(point.getX() - mid.getX(),
							mid.getY() + point.getY(),
							point.getZ() - mid.getZ());
					mid.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, locs.get(id), 0, dir.getX(), dir.getY(), dir.getZ(), 0.5);
				}
			}
		}, 1, 1);

	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (isActive())
		if (items.size() < MAX_ITEMS && state == STATE.WAITING) {
			trackItem(e.getItemDrop(), e.getPlayer());
		}
	}

	private void trackItem(Item e, Player p){
		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), new CancellableBukkitTask() {
			@Override
			public void run() {
				if (e.getLocation().getY() < 0) {
					this.cancel();
					return;
				}
				if (items.size() < MAX_ITEMS && state == STATE.WAITING) {
					if (e.isOnGround() && e != null
							&& !e.isDead()
							&& !e.isGlowing()
							&& e.getCustomName() == null) {
						if (e.getLocation().getBlockX() == l.getBlockX()
								&& e.getLocation().distance(l.clone().add(0, 1, 0)) <= 1.5
								&& e.getLocation().getBlockZ() == l.getBlockZ()) {
							ItemStack i = e.getItemStack().clone();
							ItemStack copy = e.getItemStack().clone();

							boolean wasWand = false;

							if (Wand.isItemWand(i)) {
								if (!hasWand()) {
									wand = e;
									wasWand = true;
									e.teleport(wandSpinnerLocations.get(wandSpinnerLocation).clone().add(0, 0.5, 0));
								}else {
									this.cancel();
									return;
								}
							}

							if (i.getAmount() > 1) {
								i.setAmount(1);
								copy.setAmount(copy.getAmount() - 1);
								e.setItemStack(i);
								Item i2 = l.getWorld().dropItem(e.getLocation().getBlock().getLocation().add(0.5, 0.2, 0.5), copy);
								trackItem(i2, p);
								i2.setVelocity(new Vector(0, 0, 0));
								i2.setPickupDelay(e.getPickupDelay());
							}
							e.setCustomName(UUID.randomUUID().toString().substring(0, 8));
							if (i.getItemMeta().getDisplayName() != null) {
								e.setCustomName(i.getItemMeta().getDisplayName());
								e.setCustomNameVisible(true);
							}
							e.setVelocity(new Vector(0, 0, 0));
							if (!wasWand) {
								items.add(e);
							}
							e.setGravity(false);
							l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getLocation(), 5, 0.1, 0.1, 0.1, 0.3);
							e.setPickupDelay(99999);
							Bukkit.getPluginManager().callEvent(new EventAddItemToRecipe(instance, i, p));
							this.cancel();
							return;
						}
					}
				}
			}
		}, 1, 1).getTaskId();
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
		return super.isActive();
	}

	public static String getStaticSaveFileName(){
		return "magiccrafter";
	}

	@Override
	public String getSaveFileName() {
		return getStaticSaveFileName();
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getStringFromLocation(l);
	}

	@Override
	public void shutdownCall() {
		if (craftingItemObject != null && !craftingItemObject.isDead()){
			if (state == STATE.WAITING) {
				l.getWorld().dropItemNaturally(l.clone().add(0.5, 2, 0.5), craftingItemObject.getItemStack());
			}
			craftingItemObject.remove();
		}
		for (Item i : items) {
			l.getWorld().dropItemNaturally(l.clone().add(0.5, 2, 0.5), i.getItemStack());
			i.remove();
		}
		if (hasWand()){
			l.getWorld().dropItemNaturally(l.clone().add(0.5, 2, 0.5), getWand().getItemStack());
			getWand().remove();
		}
		MagicaMain.getMagicaMain().getStorageManager().save(this, "isActive", super.isActive());
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
		return l.clone();
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.PORTAL, l.clone().add(0.5, 0.5, 0.5), 10, 0, 0, 0, 1);
	}
}
