package space.gatt.magicaproject.objects;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.List;

public class MagicCrafter implements MagicaBlock, Saveable, Listener{
	public static void registerListener(){
		Bukkit.getPluginManager().registerEvents(new Listener(){
			@EventHandler
			public void onPlace(BlockPlaceEvent e){
				if (e.getBlockPlaced().getType() == Material.ENCHANTMENT_TABLE) {
					try {
						ItemStack is = (ItemStack) MagicCrafter.class.getMethod("getStaticCraftedItem").invoke(this);
						if (BaseUtils.matchItem(e.getItemInHand(), is)) {
							MagicCrafter mc = new MagicCrafter(e.getBlock().getLocation());
							MagicaMain.getMagicaMain().getBlockManager().registerBlock(mc);
						}
					} catch (Exception ignored) {
						ignored.printStackTrace();
					}
				}
			}
		},  MagicaMain.getMagicaMain());
	}

	private Location l;
	private boolean isActive;

	public MagicCrafter(Location l) {
		this.l = l;
		l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		shutdownCall();
		isActive = true;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if (e.getBlock().getLocation().toString().equalsIgnoreCase(l.toString())){ // Bukkit didn't like checking between two locations
			MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
			MagicaMain.getMagicaMain().getStorageManager().removeFromSave(this);
			isActive = false;
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if (e.getAction().name().contains("RIGHT")){
			if (e.getClickedBlock().getLocation().toString().equalsIgnoreCase(l.toString())){
				e.setUseItemInHand(Event.Result.DENY);
				e.setUseInteractedBlock(Event.Result.DENY);
				Inventory inv = Bukkit.createInventory(null, InventoryType.DISPENSER, BaseUtils.colorString("&bMagic Crafter"));
				e.getPlayer().openInventory(inv);
			}
		}
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String getSaveFileName() {
		return "magiccrafter";
	}

	@Override
	public String getSaveFileFolder() {
		return "blocks/" + BaseUtils.getFileNameFromLocation(l);
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "class", this.getClass());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-x", l.getX());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-y", l.getY());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-z", l.getZ());
		MagicaMain.getMagicaMain().getStorageManager().save(this, "location-world", l.getWorld().getName());
	}

	@Override
	public void loadCall(JsonObject loadedObject) {
	}

	@Override
	public Location getLocation() {
		return l;
	}

	@Override
	public void runParticles() {
		l.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, l.clone().add(0.5, 0.5, 0.5), 100, 0, 0, 0, 1);
		l.getWorld().spawnParticle(Particle.PORTAL, l.clone().add(0.5, 0.5, 0.5), 100, 0, 0, 0, 1);
	}

	@Override
	public String getBlockName() {
		return "Magica Crafter";
	}

	@Override
	public ItemStack getCraftedItem() {
		return MagicCrafter.getStaticCraftedItem();
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack magicCrafter = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemMeta im = magicCrafter.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&b&lMana Crafter"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		magicCrafter.setItemMeta(im);
		return magicCrafter;
	}

	@Override
	public Material getInventoryMaterial() {
		return Material.ENCHANTMENT_TABLE;
	}

	@Override
	public List<ItemStack> getItemRecipe() {
		return null;
	}
}
