package space.gatt.magicaproject.objects;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedManaGenerator extends Craftable implements MagicaBlock, Saveable, ManaStorable {
	private Location l;
	private OfflinePlayer playerPlaced;
	private float storedMana;
	public AdvancedManaGenerator(Location l, OfflinePlayer playerPlaced) {
		this.l = l;
		this.playerPlaced = playerPlaced;
		shutdownCall();
	}

	public static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onItemAdd(EventAddItemToRecipe e) {
				if (e.getCrafter().getItems().size() == getStaticRecipe().size()){
					if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), getStaticRecipe())){
						e.getCrafter().beginCrafting(AdvancedManaGenerator.class, 1000, 2, e.getPlayer());
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	@Override
	public List<ItemStack> getItemRecipe() {
		return getStaticRecipe();
	}

	public static List<ItemStack> getStaticRecipe() {
		List<ItemStack> items = new ArrayList<>(Arrays.asList(
				ManaGenerator.getStaticCraftedItem(),
				new ItemStack(Material.NETHER_STAR),
				new ItemStack(Material.TOTEM),
				new ItemStack(Material.WHITE_SHULKER_BOX)));
		return items;
	}

	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.DRAGON_BREATH, l.clone().add(0.5, 0.5, 0.5), 7, 0.4, 0.4, 0.4, 0);
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public String getItemName() {
		return "Mana Generator";
	}

	@Override
	public ItemStack getCraftedItem(){
		return getStaticCraftedItem();
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.WHITE_SHULKER_BOX);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&aAdvanced Mana Generator"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public Material getInventoryMaterial() {
		return Material.WHITE_SHULKER_BOX;
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
	public String getSaveFileName() {
		return "advancedmanagenerator";
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getStringFromLocation(l);
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "class", this.getClass());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", l.getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", l.getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", l.getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", l.getWorld().getName());
		MagicaMain.getMagicaMain().getStorageManager().save(this, BaseUtils.getStringFromLocation(l) + "-managenerator-player", playerPlaced);
		MagicaMain.getMagicaMain().getStorageManager().save(this, BaseUtils.getStringFromLocation(l) + "-managenerator-storedmana", storedMana);
	}

	@Override
	public void loadCall(JsonObject loadedObject) {

	}
}
