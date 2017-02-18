package space.gatt.magicaproject.objects.blocks;

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
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

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
				for (MagicaRecipe recipe : getStaticRecipes()){
					if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), recipe.getRequirements())) {
						e.getCrafter().beginCrafting(recipe.getCraftedItem(), recipe.getTimeInTicks(), recipe.getManaPerTick(), e.getPlayer());
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				ManaGenerator.getStaticCraftedItem(),
				new ItemStack(Material.NETHER_STAR),
				new ItemStack(Material.TOTEM),
				new ItemStack(Material.WHITE_SHULKER_BOX))), getStaticCraftedItem(), 1000, 2);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
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
