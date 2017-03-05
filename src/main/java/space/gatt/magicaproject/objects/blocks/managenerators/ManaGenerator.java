package space.gatt.magicaproject.objects.blocks.managenerators;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ManaGenerator extends MagicaBlock implements Craftable, Saveable, ManaStorable, Listener {
	private Location l;
	private OfflinePlayer playerPlaced;
	private float storedMana;

	public ManaGenerator(Location l, OfflinePlayer playerPlaced) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		this.l = l;
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		this.playerPlaced = playerPlaced;
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	public ManaGenerator(JsonObject object){
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
		OfflinePlayer of;
		if (object.has("player-uuid")){
			of = Bukkit.getOfflinePlayer(UUID.fromString(object.get("player-uuid").getAsString()));
			this.playerPlaced = of;
		}
		this.l = new Location(world, x, y, z);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive())
			if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				if (e.getPlayer().getUniqueId() != playerPlaced.getUniqueId()){
					e.setCancelled(true);
					e.getPlayer().sendMessage(BaseUtils.colorString("&cThis isn't your Mana Generator!"));
					return;
				}
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				super.setActive(false);
			}
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(new ItemStack(Material.DIAMOND),
				new ItemStack(Material.EMERALD),
				new ItemStack(Material.ENDER_PEARL),
				new ItemStack(Material.ENDER_PEARL),
				new ItemStack(Material.BLAZE_POWDER),
				new ItemStack(Material.CHEST),
				MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 100, 0);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = MagicaMain.getBaseStack();
		manaGenerator.setDurability((short)2);
		ItemMeta im = manaGenerator.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMana Generator"));
		im.setLore(MagicaMain.getLoreLine());
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}


	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.DRAGON_BREATH, l.clone().add(0.5, 0.5, 0.5), 5, 0.25, 0.25, 0.25, 0);
		l.getWorld().playSound(l, Sound.BLOCK_LAVA_POP, SoundCategory.MASTER, 0.1f, 2);
		increaseMana(1);
		if (getManaLevel() > 1000){
			setManaLevel(1000f);
		}
	}

	@Override
	public boolean isActive() {
		return super.isActive();
	}

	@Override
	public String getItemName() {
		return "Mana Generator";
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

	public static String getStaticSaveFileName(){
		return "managenerator";
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
		MagicaMain.getMagicaMain().getStorageManager().save(this, "player", playerPlaced.getName());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "player-uuid", playerPlaced.getUniqueId());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "storedmana", storedMana);
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}
}