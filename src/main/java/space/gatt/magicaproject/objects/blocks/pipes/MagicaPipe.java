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
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.Wrench;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
			return locations.toArray(new Location[locations.size() - 1]);
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
				if (b.getMetadata("MagicaObject").get(0).value() instanceof ManaStorable) {
					locations.add((MagicaBlock)b.getMetadata("MagicaObject").get(0).value());
				}
			}
		}
		if (locations.size() > 0) {
			return locations.toArray(new MagicaBlock[locations.size() - 1]);
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
		if (nearbyPipes != null && nearbyPipes.length > 0){
			Location first = nearbyPipes[0];
			BlockFace touching = first.getBlock().getFace(l.getBlock());
			byte face = (byte)1;
			if (touching == BlockFace.DOWN){
				face = 0;
			}
			if (touching == BlockFace.NORTH){
				face = 2;
			}
			if (touching == BlockFace.SOUTH){
				face = 3;
			}
			if (touching == BlockFace.WEST){
				face = 4;
			}
			if (touching == BlockFace.EAST){
				face = 5;
			}
			l.getBlock().setData(face);
		}
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
		super.setLocation(new Location(world, x, y, z));
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		super.getLocation().getBlock().setMetadata("isPipe", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
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
					if (e.getPlayer().isSneaking()){
						Location[] nearbyPipes = getNearbyPipes();
						if (nearbyPipes != null && nearbyPipes.length > 0){
							Location first = nearbyPipes[new Random().nextInt(nearbyPipes.length - 1)];
							BlockFace touching = first.getBlock().getFace(super.getLocation().getBlock());
							byte face = (byte)1;
							if (touching == BlockFace.DOWN){
								face = 0;
							}
							if (touching == BlockFace.NORTH){
								face = 2;
							}
							if (touching == BlockFace.SOUTH){
								face = 3;
							}
							if (touching == BlockFace.WEST){
								face = 4;
							}
							if (touching == BlockFace.EAST){
								face = 5;
							}
							super.getLocation().getBlock().setData(face);
						}
						return;
					}
					if (e.getClickedBlock().getData() < 7){
						byte current = e.getClickedBlock().getData();
						if (current == (byte)0){
							e.getClickedBlock().setData((byte)1);
							return;
						}
						if (current == (byte)1){
							e.getClickedBlock().setData((byte)2);
							return;
						}
						if (current == (byte)2){
							e.getClickedBlock().setData((byte)3);
							return;
						}
						if (current == (byte)3){
							e.getClickedBlock().setData((byte)4);
							return;
						}
						if (current == (byte)4){
							e.getClickedBlock().setData((byte)5);
							return;
						}
						if (current == (byte)5){
							e.getClickedBlock().setData((byte)6);
							return;
						}
						if (current == (byte)6){
							e.getClickedBlock().setData((byte)0);
							return;
						}
					}
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
			if (super.getLocation().getBlock().getData() == 0) {
				touching = BlockFace.UP;
			}
			if (super.getLocation().getBlock().getData() == 2) {
				touching = BlockFace.SOUTH;
			}
			if (super.getLocation().getBlock().getData() == 3) {
				touching = BlockFace.NORTH;
			}
			if (super.getLocation().getBlock().getData() == 4) {
				touching = BlockFace.EAST;
			}
			if (super.getLocation().getBlock().getData() == 5) {
				touching = BlockFace.WEST;
			}
			MagicaBlock blockTouching =
					MagicaBlock.getMagicaBlockAtLocation(super.getLocation().getBlock().getRelative(touching));
			if (blockTouching instanceof ManaStorable) {

				ManaStorable ms = (ManaStorable) blockTouching;
				if (ms.getManaLevel() > 50) {
					if (getManaLevel() + 50 <= 300) {
						ms.decreaseMana(50);
						increaseMana(50);
						Bukkit.broadcastMessage(ChatColor.DARK_RED + "Stole 50 mana!  (" + ms.getManaLevel() + " remaining in container) (I now have " + (getManaLevel() + ms.getManaLevel()) + ")");
					}
				} else {
					if (ms.getManaLevel() >= 15 && ms.getManaLevel() < 50 && getManaLevel() + ms.getManaLevel() <= 300) {
						Bukkit.broadcastMessage(ChatColor.RED + "Stole " + ms.getManaLevel() + " mana! (0 remaining in container) (I now have " + (getManaLevel() + ms.getManaLevel()) + ")");
						ms.decreaseMana(ms.getManaLevel());
						increaseMana(ms.getManaLevel());
					}
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
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", super.getLocation().getWorld().getName());
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

	@Override
	public float getManaLevel() {
		return manaStored;
	}

	@Override
	public void setManaLevel(float f) {
		this.manaStored = f;
		if (manaStored > 300){
			manaStored = 300;
		}
	}

	@Override
	public float increaseMana(float f) {

		manaStored += f;
		if (manaStored > 300){
			manaStored = 300;
		}
		return manaStored;
	}

	@Override
	public float decreaseMana(float f) {
		manaStored -= f;
		if (manaStored > 300){
			manaStored = 300;
		}
		return manaStored;
	}
}
