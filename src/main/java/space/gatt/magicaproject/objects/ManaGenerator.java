package space.gatt.magicaproject.objects;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.Arrays;
import java.util.List;

public class ManaGenerator implements MagicaBlock, Saveable, ManaStorable{
	private Location l;
	private OfflinePlayer playerPlaced;
	private float storedMana;

	public ManaGenerator(Location l, OfflinePlayer playerPlaced) {
		this.l = l;
		this.playerPlaced = playerPlaced;
		shutdownCall();
	}

	@Override
	public List<ItemStack> getItemRecipe() {
		List<ItemStack> items = Arrays.asList();
		return items;
	}

	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.DRAGON_BREATH, l.clone().add(0.5, 0.5, 0.5), 30, 0.3, 0.3, 0.3);
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public String getBlockName() {
		return "Mana Generator";
	}

	@Override
	public ItemStack getCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.CYAN_SHULKER_BOX);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&bMana Generator"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public Material getInventoryMaterial() {
		return Material.CYAN_SHULKER_BOX;
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
		return "managenerator";
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
