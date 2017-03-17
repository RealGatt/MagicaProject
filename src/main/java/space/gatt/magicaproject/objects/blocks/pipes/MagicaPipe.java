package space.gatt.magicaproject.objects.blocks.pipes;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.*;
import space.gatt.magicaproject.objects.items.Wrench;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MagicaPipe extends MagicaBlock implements Craftable, Saveable, Listener, ManaStorable {

	public static void registerListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			private void onPlace(BlockPlaceEvent e){
				if (e.canBuild() && !e.isCancelled() && BaseUtils.matchItem(e.getItemInHand(), getStaticCraftedItem())){
					new MagicaPipe(e.getBlockPlaced().getLocation());
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	private float manaStored = 0;
	private float transferSpeed = 1;


	public Location[] getNearbyPipes(){
		ArrayList<Location> locations = new ArrayList<>();
		BlockFace[] checking = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
		for (BlockFace bf : checking){
			Block b = super.getLocation().getBlock().getRelative(bf);
			if (b.getType() == getStaticCraftedItem().getType()){
				if (b.hasMetadata("isPipe") && b.getMetadata("isPipe").get(0).asBoolean()){
					locations.add(b.getLocation());
				}
			}
		}

		if (locations.size() > 0) {
			Collections.shuffle(locations);
			return locations.toArray(new Location[locations.size()]);
		}else{
			return null;
		}
	}

	public MagicaBlock[] getNearbyManaStorages(){
		ArrayList<MagicaBlock> locations = new ArrayList<>();
		BlockFace[] checking = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
		for (BlockFace bf : checking){
			Block b = super.getLocation().getBlock().getRelative(bf);
			if (b.hasMetadata("MagicaObject")){
				if (b.getMetadata("MagicaObject").get(b.getMetadata("MagicaObject").size() - 1).value() instanceof ManaStorable) {
					locations.add((MagicaBlock) (b.getMetadata("MagicaObject").get(0).value()));
				}
			}
		}

		if (locations.size() > 0) {
			Collections.shuffle(locations);
			return locations.toArray(new MagicaBlock[locations.size()]);
		}else{
			return null;
		}
	}



	public MagicaPipe(Location l) {
		super(l);
		super.setDisplayedInSpawner(false);
		super.setLocation(l);
		super.setActive(true);
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		l.getBlock().setMetadata("isPipe", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
		Location[] nearbyPipes = getNearbyPipes();
	}

	public MagicaPipe(JsonObject object) {
		super(object);
		super.setDisplayedInSpawner(false);

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
		if (object.has("transferspeed")){
			setTransferSpeed(object.get("transferspeed").getAsFloat());
		}
		super.setLocation(new Location(world, x, y, z));
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		super.getLocation()
				.getBlock().setMetadata("isPipe", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
		updateUpgrades();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive())
			if (e.getBlock().getLocation().equals(super.getLocation())) {
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), getStaticCraftedItem());
				}
				super.setActive(false);
			}
	}

	@EventHandler
	public void onWrenchUse(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && super.isActive()){
			if (e.getClickedBlock().getLocation().equals(super.getLocation())){
				if (BaseUtils.matchItem(e.getItem(), Wrench.getStaticCraftedItem())){
					if (e.getClickedBlock().getData() < 6){
						byte next = (byte)(e.getClickedBlock().getData() + 1);
						e.getClickedBlock().setData(next);
					}else{
						e.getClickedBlock().setData((byte)0);
					}
					e.getClickedBlock().getState().update();
				}
			}
		}
	}
	// 0 = down
	// 1 = up
	// 2 = north
	// 3 = south
	// 4 = west
	// 5 = east
	@Override
	public void runParticles() {
		super.getLocation().getWorld().spawnParticle(Particle.DRAGON_BREATH, super.getLocation().clone().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
		if (getNearbyManaStorages() != null) {
			BlockFace touching = BlockFace.DOWN;
			BlockFace opposite = BlockFace.UP;
			if (super.getLocation().getBlock().getData() == 0) {
				touching = BlockFace.UP;
				opposite = BlockFace.DOWN;
			}
			if (super.getLocation().getBlock().getData() == 2) {
				touching = BlockFace.SOUTH;
				opposite = BlockFace.NORTH;
			}
			if (super.getLocation().getBlock().getData() == 3) {
				touching = BlockFace.NORTH;
				opposite = BlockFace.SOUTH;
			}
			if (super.getLocation().getBlock().getData() == 4) {
				touching = BlockFace.EAST;
				opposite = BlockFace.WEST;
			}
			if (super.getLocation().getBlock().getData() == 5) {
				touching = BlockFace.WEST;
				opposite = BlockFace.EAST;
			}
			MagicaBlock blockTouching =
					MagicaBlock.getMagicaBlockAtLocation(super.getLocation().getBlock().getRelative(touching));
			MagicaBlock blockTouchingOpposite =
					MagicaBlock.getMagicaBlockAtLocation(super.getLocation().getBlock().getRelative(opposite));
			if (blockTouching instanceof ManaStorable) {

				ManaStorable ms = (ManaStorable) blockTouching;
				Location inBetween = getLocation().clone().add(0.5, 0.5, 0.5).toVector().
						midpoint(blockTouching.getLocation().clone().add(0.5, 0.5, 0.5).toVector())
						.toLocation(getLocation().getWorld());
				if (ms.allowsOutput()) {
					for (int count = 0; count < getTransferSpeed(); count++) {
						if (ms.getManaLevel() > 0 && getManaLevel() + 1 <= getMaxMana()) {
							ms.decreaseMana(1);
							increaseMana(1);
						}
					}
				} else {
					getLocation().getWorld().spawnParticle(Particle.REDSTONE, inBetween, 1, 0, 0, 0, 0);
				}

			}
			if (blockTouchingOpposite instanceof ManaStorable) {
				ManaStorable ms = (ManaStorable) blockTouchingOpposite;
				Location inBetween = getLocation().clone().add(0.5, 0.5, 0.5).toVector().
						midpoint(blockTouchingOpposite.getLocation().clone().add(0.5, 0.5, 0.5).toVector())
						.toLocation(getLocation().getWorld());
				if (ms.acceptsInput()) {
					for (int count = 0; count < getTransferSpeed(); count++) {
						if (getManaLevel() > 0 && ms.getManaLevel() + 1 <= ms.getMaxMana()) {
							ms.increaseMana(1);
							decreaseMana(1);
						}
					}
				}else{
					getLocation().getWorld().spawnParticle(Particle.REDSTONE, inBetween, 1, 0, 0, 0, 0);
				}
			}
		}
	}

	public static String getStaticSaveFileName(){
		return "magicapipe";
	}


	@Override
	public String getSaveFileName() {
		return getStaticSaveFileName();
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getStringFromLocation(super.getLocation());
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "isActive", super.isActive());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", super.getLocation().getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", super.getLocation().getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", super.getLocation().getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "storedmana", getManaLevel());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "transferspeed", getTransferSpeed());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", super.getLocation().getWorld().getName());
	}

	public float getTransferSpeed() {
		return transferSpeed;
	}

	public void setTransferSpeed(float transferSpeed) {
		this.transferSpeed = transferSpeed;
	}

	public void increaseTransferSpeed(float amount) {
		this.transferSpeed += amount;
	}

	public void decreaseTransferSpeed(float amount) {
		this.transferSpeed -= amount;
	}

	@Override
	public String getItemName() {
		return "Magica Pipe";
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}

	private static ItemStack getStaticCraftedItem(){
		ItemStack is = new ItemStack(Material.END_ROD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&fMagica Pipe"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Allows for the transportation of Magica"),
				BaseUtils.colorString("&7 Can be rotated with the &9Magic Wrench"),
				BaseUtils.colorString("&7 Requires a &9Magica Redirector&7 to change directions."),
				MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		is.setItemMeta(im);
		return is;
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLASS),
				new ItemStack(Material.GLOWSTONE_DUST)
		)), getStaticCraftedItem(), 1000, 0, 8, true);
		recipes.add(recipe);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	private float maxMana = 300;

	@Override
	public float getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(float amt) {
		maxMana = amt;
	}

	@Override
	public boolean acceptsInput() {
		return true;
	}

	@Override
	public boolean allowsOutput() {
		return true;
	}

	@Override
	public boolean acceptsUpgrade(UpgradeType type) {
		return type == UpgradeType.SPEED_UPGRADE || type == UpgradeType.CAPACITY_UPGRADE;
	}

	@Override
	public void applyUpgrade(UpgradeType upgrade) {
		if (acceptsUpgrade(upgrade)) {
			super.applyUpgrade(upgrade);
			updateUpgrades();
		}
	}

	private void updateUpgrades(){
		for (UpgradeType upgrade : super.getAppliedUpgrades()) {
			if (upgrade == UpgradeType.SPEED_UPGRADE) {
				UpgradeInstance upI = super.getUpgrade(upgrade);
				setTransferSpeed((upI.getLevel() * 10) + 20);
			}
			if (upgrade == UpgradeType.CAPACITY_UPGRADE){
				UpgradeInstance upI = super.getUpgrade(upgrade);
				setMaxMana((upI.getLevel() * 25) + 300);
			}
		}
	}

	@Override
	public float getManaLevel() {
		return manaStored;
	}

	@Override
	public void setManaLevel(float f) {
		this.manaStored = f;
		if (manaStored > getManaLevel()){
			manaStored = getManaLevel();
		}
	}

	@Override
	public float increaseMana(float f) {

		manaStored += f;
		if (manaStored > getManaLevel()){
			manaStored = getManaLevel();
		}
		return manaStored;
	}

	@Override
	public float decreaseMana(float f) {
		manaStored -= f;
		if (manaStored > getManaLevel()){
			manaStored = getManaLevel();
		}
		return manaStored;
	}
}
