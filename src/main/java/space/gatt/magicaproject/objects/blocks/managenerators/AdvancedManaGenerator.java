package space.gatt.magicaproject.objects.blocks.managenerators;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.BlockDisplayName;
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

public class AdvancedManaGenerator extends MagicaBlock implements Craftable, Saveable, ManaStorable, Listener {
	private Location l;
	private OfflinePlayer playerPlaced;
	private float storedMana;
	private BlockDisplayName blockDisplayName;

	public AdvancedManaGenerator(Location l, OfflinePlayer playerPlaced) {
		super(l);
		super.setLocation(l);
		super.setActive(true);
		this.l = l;
		super.setDisplayedItem(getStaticCraftedItem());
		super.updateBlock();
		this.playerPlaced = playerPlaced;
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		blockDisplayName = new BlockDisplayName(this, "&7Mana Stored: &b0", 20);
	}

	public AdvancedManaGenerator(JsonObject object){
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
		if (object.has("storedmana")){
			setManaLevel(object.get("storedmana").getAsFloat());
		}
		this.l = new Location(world, x, y, z);
		super.setLocation(l);
		super.setActive(true);
		super.setDisplayedItem(getStaticCraftedItem());
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		super.updateBlock();
		MagicaMain.getMagicaMain().getBlockManager().registerBlock(this);
		blockDisplayName = new BlockDisplayName(this, "&7Mana Stored: &b0", 1);
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				ManaGenerator.getStaticCraftedItem(),
				new ItemStack(Material.NETHER_STAR),
				MagicaShard.getStaticCraftedItem(), MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 1000, 2, true);
		recipes.add(rec1);
		return recipes;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (isActive()){
			if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				if (e.getPlayer().getUniqueId() != playerPlaced.getUniqueId()) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(BaseUtils.colorString("&cThis isn't your Mana Generator!"));
					return;
				}
				MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
				MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
				super.setActive(false);
				blockDisplayName.destroy();
			}
		}
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	@Override
	public Location getLocation() {
		return l.clone();
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.DRAGON_BREATH, l.clone().add(0.5, 0.5, 0.5), 1, 0.25, 0.25, 0.25, 0);
		increaseMana(5);
		blockDisplayName.setDisplay("&7Mana Stored: &b" + getManaLevel() + "/" + getMaxMana());
	}

	@Override
	public boolean isActive() {
		return super.isActive();
	}

	@Override
	public String getItemName() {
		return "Mana Generator";
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = MagicaMain.getBaseStack();
		manaGenerator.setDurability((short)3);
		ItemMeta im = manaGenerator.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&aAdvanced Mana Generator"));
		im.setLore(MagicaMain.getLoreLine());
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public boolean acceptsInput() {
		return false;
	}

	@Override
	public boolean allowsOutput() {
		return true;
	}

	@Override
	public float getMaxMana() {
		return 100000;
	}

	@Override
	public float getManaLevel() {
		return storedMana;
	}

	@Override
	public void setManaLevel(float f) {
		this.storedMana = f;
		if (storedMana > getMaxMana()){
			storedMana = getMaxMana();
		}
	}

	@Override
	public float increaseMana(float f) {

		storedMana += f;
		if (storedMana > getMaxMana()){
			storedMana = getMaxMana();
		}
		return storedMana;
	}

	@Override
	public float decreaseMana(float f) {
		storedMana -= f;
		if (storedMana > getMaxMana()){
			storedMana = getMaxMana();
		}
		return storedMana;
	}

	public static String getStaticSaveFileName(){
		return "advancedmanagenerator";
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
		blockDisplayName.destroy();
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}
}
