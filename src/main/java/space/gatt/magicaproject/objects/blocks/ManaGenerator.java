package space.gatt.magicaproject.objects.blocks;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ManaGenerator extends Craftable implements MagicaBlock, Saveable, ManaStorable, Listener {
	private Location l;
	private OfflinePlayer playerPlaced;
	private float storedMana;
	private boolean isActive;

	public ManaGenerator(Location l, OfflinePlayer playerPlaced) {
		this.l = l;
		this.playerPlaced = playerPlaced;
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		isActive = true;
		shutdownCall();
	}

	public ManaGenerator(JsonObject object){

	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (isActive())
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getLocation().toString().equalsIgnoreCase(l.toString())) {
				e.setUseItemInHand(Event.Result.DENY);
				e.setUseInteractedBlock(Event.Result.DENY);
			}
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
				isActive = false;
			}
	}

	public static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlace(BlockPlaceEvent e) {
				if (e.getBlockPlaced().getType() == getStaticCraftedItem().getType()) {
					ItemStack is = getStaticCraftedItem();
					if (BaseUtils.matchItem(e.getItemInHand(), is)) {
						ManaGenerator mg = new ManaGenerator(e.getBlock().getLocation(), (OfflinePlayer) e.getPlayer());
						MagicaMain.getMagicaMain().getBlockManager().registerBlock(mg);
					}
				}
			}

		}, MagicaMain.getMagicaMain());
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
		ItemStack manaGenerator = new ItemStack(Material.CYAN_SHULKER_BOX);
		ItemMeta im = manaGenerator.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMana Generator"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(MagicaMain.getLoreLine());
		im.setUnbreakable(true);
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}


	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.DRAGON_BREATH, l.clone().add(0.5, 0.5, 0.5), 5, 0.4, 0.4, 0.4, 0);
	}

	@Override
	public boolean isActive() {
		return isActive;
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
