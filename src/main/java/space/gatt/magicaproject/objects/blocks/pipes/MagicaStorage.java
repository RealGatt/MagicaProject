package space.gatt.magicaproject.objects.blocks.pipes;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.extra.BlockDisplayName;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.*;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.objects.items.Wrench;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicaStorage extends MagicaBlock implements Craftable, Saveable, ManaStorable, Listener {
	private Location l;
	private float storedMana;
	private BlockDisplayName blockDisplayName;
	private boolean displayMana = true;

	public MagicaStorage(Location l, OfflinePlayer playerPlaced) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		super.setActive(true);
		this.l = l;
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		blockDisplayName = new BlockDisplayName(this, "&7Mana Stored: &b0", 1);
	}

	public MagicaStorage(JsonObject object){
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
		if (object.has("storedmana")){
			setManaLevel(object.get("storedmana").getAsFloat());
		}
		if (object.has("displaymana")){
			displayMana = object.get("displaymana").getAsBoolean();
		}
		this.l = new Location(world, x, y, z);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		blockDisplayName = new BlockDisplayName(this, "&7Mana Stored: &b" + getManaLevel(), 1);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive()) {
			if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				blockDisplayName.destroy();
				super.setActive(false);
			}
		}
	}

	@EventHandler
	public void onWrench(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getLocation().equals(super.getLocation()) && e.hasItem()) {
				if (BaseUtils.matchItem(e.getItem(), Wrench.getStaticCraftedItem())) {
					displayMana = !displayMana;
				}
			}
		}
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.ENDER_PEARL),
				new ItemStack(Material.CHEST),
				MagicaShard.getStaticCraftedItem(), MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 100, 0);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = MagicaMain.getBaseBlockStack();
		manaGenerator.setDurability((short)6);
		ItemMeta im = manaGenerator.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMana Storage"));
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
		blockDisplayName.setDoesDisplayName(displayMana);
		blockDisplayName.setDisplay("&7Mana Stored: &b" + getManaLevel() + "/" + getMaxMana());
	}

	@Override
	public String getItemName() {
		return "Mana Storage";
	}

	@Override
	public boolean acceptsInput() {
		return true;
	}

	@Override
	public boolean allowsOutput() {
		return true;
	}

	private float maxMana = 100000;

	@Override
	public float getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(float amt) {
		maxMana = amt;
	}

	@Override
	public float getManaLevel() {
		return storedMana;
	}

	@Override
	public void setManaLevel(float f) {
		storedMana = f;
	}

	@Override
	public float increaseMana(float f) {
		storedMana += f;
		return storedMana;
	}

	@Override
	public float decreaseMana(float f) {
		storedMana -= f;
		return storedMana;
	}

	@Override
	public boolean acceptsUpgrade(UpgradeType type) {
		return type == UpgradeType.CAPACITY_UPGRADE;
	}

	@Override
	public void applyUpgrade(UpgradeType upgrade) {
		if (acceptsUpgrade(upgrade)) {
			super.applyUpgrade(upgrade);
			UpgradeInstance upI = super.getUpgrade(upgrade);
			if (upgrade == UpgradeType.CAPACITY_UPGRADE){
				setMaxMana((upI.getLevel() * 25) + 100000);
			}
		}
	}

	public static String getStaticSaveFileName(){
		return "manastorage";
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
		MagicaMain.getMagicaMain().getStorageManager().save(this, "storedmana", storedMana);
		MagicaMain.getMagicaMain().getStorageManager().save(this, "displaymana", displayMana);
		blockDisplayName.destroy();
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}
}
