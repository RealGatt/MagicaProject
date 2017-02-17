package space.gatt.magicaproject.objects;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
							MagicCrafter mc = new MagicCrafter(e.getBlockPlaced().getLocation());
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

	public MagicCrafter(Location l) {
		this.l = l;
		l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
		shutdownCall();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if (e.getBlock() == l.getBlock()){
			MagicaMain.getMagicaMain().getBlockManager().removeBlock(this);
		}
	}

	@Override
	public String getSaveFileName() {
		return "blocks/" + BaseUtils.getStringFromLocation(l) + "-magiccrafters";
	}

	@Override
	public void shutdownCall() {
		MagicaMain.getMagicaMain().getStorageManager().save(this, "magiccrafter-location", l);
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
