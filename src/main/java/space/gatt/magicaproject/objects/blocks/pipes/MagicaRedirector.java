package space.gatt.magicaproject.objects.blocks.pipes;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.*;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagicaRedirector extends MagicaBlock implements Craftable, Saveable, ManaStorable, Listener, MagicaInventory {
	private Location l;
	private Inventory inv;

	public MagicaRedirector(Location l) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		super.setActive(true);
		this.l = l;
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		inv = Bukkit.createInventory(null, InventoryType.DISPENSER, "Mana Redirector");
	}

	public MagicaRedirector(JsonObject object){
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
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		inv = Bukkit.createInventory(null, InventoryType.DISPENSER,"Mana Redirector");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (super.isActive() && !e.isCancelled() && e.useInteractedBlock() == Event.Result.ALLOW && e.useItemInHand() == Event.Result.DENY) {
				if (e.getClickedBlock().getLocation().equals(super.getLocation())) {
					if (!e.getPlayer().isSneaking()) {
						e.getPlayer().openInventory(inv);
						e.setCancelled(true);
						e.setUseItemInHand(Event.Result.DENY);
					}else{
						e.setUseInteractedBlock(Event.Result.DENY);
						e.setUseItemInHand(Event.Result.ALLOW);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive()) {
			if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				super.setActive(false);
				if (inv.getContents().length > 0) {
					for (ItemStack is : inv.getStorageContents()) {
						if (is != null) {
							super.getLocation().getWorld().dropItemNaturally(super.getLocation(), is);
						}
					}
				}
				inv.clear();
				inv = null;
			}
		}
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				MagicaPipe.getStaticCraftedItem(),
				MagicaStorage.getStaticCraftedItem(),
				MagicaShard.getStaticCraftedItem(),
				MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 500);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = MagicaMain.getBaseBlockStack();
		manaGenerator.setDurability((short)7);
		ItemMeta im = manaGenerator.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMana Redirector"));
		im.setLore(MagicaMain.getLoreLine());
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}


	@Override
	public Location getLocation() {
		return l.clone();
	}

	@Override
	public void runParticles() {
		if (getManaLevel() > 5) {
			if (inv.getStorageContents().length > 0) {
				super.getLocation().getWorld().spawnParticle(
						Particle.DRAGON_BREATH,
						super.getLocation().clone().add(0.5, 0.5, 0.5),
						1, 0.3, 0.3, 0.3, 0);
				for (ItemStack is : inv.getStorageContents()){
					if (is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
						int id = -1;
						lorecheck : for (String loreLine : is.getItemMeta().getLore()){
							id++;
							String loreLineCopy = ChatColor.stripColor(loreLine);
							if (loreLineCopy.contains("Mana Stored: ")){
								String[] parts = loreLineCopy.replaceAll("Mana Stored: ", "").split("/");
								if (parts.length > 1) {
									int stored = Integer.parseInt(parts[0]);
									int max = Integer.parseInt(parts[1]);
									int newStored = stored;
									if (stored < max) {
										if (stored + getRechargeSpeed() <= max) {
											if (getManaLevel() > getRechargeSpeed()) {
												newStored += getRechargeSpeed();
												decreaseMana(getRechargeSpeed());
											} else {
												newStored += getManaLevel();
												decreaseMana(getManaLevel());
											}
										}else{
											int dif = max - stored;
											stored = max;
											decreaseMana(dif);
										}


										loreLine = loreLine.replace(BaseUtils.colorString("&b" + stored + "&7/"),
												BaseUtils.colorString("&b" + newStored + "&7/"));
										List<String> newLore = is.getItemMeta().getLore();
										newLore.set(id, loreLine);
										ItemMeta im = is.getItemMeta();
										im.setLore(newLore);

										is.setItemMeta(im);
									}
								}
								break lorecheck;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getItemName() {
		return "Mana Redirector";
	}

	@Override
	public boolean acceptsInput() {
		return true;
	}

	@Override
	public boolean allowsOutput() {
		return false;
	}

	private float maxMana = 100000f;

	private float manaStored = 0;

	@Override
	public float getMaxMana() {
		return this.maxMana;
	}

	@Override
	public void setMaxMana(float amt) {
		maxMana = amt;
	}

	@Override
	public float getManaLevel() {
		return manaStored;
	}

	@Override
	public void setManaLevel(float f) {
		manaStored = f;
	}

	@Override
	public float increaseMana(float f) {
		manaStored += f;
		if (manaStored > getMaxMana()){
			manaStored = getMaxMana();
		}
		return manaStored;
	}

	@Override
	public float decreaseMana(float f) {
		manaStored -= f;
		if (manaStored > getManaLevel()){
			manaStored = getMaxMana();
		}
		if (manaStored < 0){
			manaStored = 0;
		}
		return manaStored;
	}

	public float getRechargeSpeed() {
		return rechargeSpeed;
	}

	public float increaseRechargeSpeed(float f) {
		rechargeSpeed += f;
		return rechargeSpeed;
	}

	public float decreaseRechargeSpeed(float f) {
		rechargeSpeed -= f;
		return rechargeSpeed;
	}

	public void setRechargeSpeed(float rechargeSpeed) {
		this.rechargeSpeed = rechargeSpeed;
	}

	private float rechargeSpeed = 15;


	@Override
	public boolean acceptsUpgrade(UpgradeType type) {
		return type == UpgradeType.SPEED_UPGRADE;
	}

	@Override
	public void applyUpgrade(UpgradeType upgrade) {
		if (acceptsUpgrade(upgrade)) {
			super.applyUpgrade(upgrade);
			UpgradeInstance upI = super.getUpgrade(upgrade);
			if (upgrade == UpgradeType.SPEED_UPGRADE){
				setRechargeSpeed((upI.getLevel() * 25) + 100);
			}
		}
	}

	public static String getStaticSaveFileName(){
		return "manaredirector";
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
	public Inventory getInventory() {
		return inv;
	}
}
