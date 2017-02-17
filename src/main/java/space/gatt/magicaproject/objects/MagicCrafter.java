package space.gatt.magicaproject.objects;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.managers.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.List;

public class MagicCrafter implements MagicaBlock, Saveable {
	static {
		Bukkit.getPluginManager().registerEvents(new Listener(){
			@EventHandler
			public void onPlace(BlockPlaceEvent e){
				try {
					ItemStack is = (ItemStack)getClass().getMethod("getCraftedItem").invoke(null);
					if (e.getItemInHand() == is){
						MagicCrafter mc = new MagicCrafter(e.getBlockPlaced().getLocation());
						MagicaMain.getMagicaMain().getBlockManager().registerBlock(mc);
					}
				}catch (Exception ignored){

				}
			}
		},  MagicaMain.getMagicaMain());
	}

	private Location l;

	public MagicCrafter(Location l) {
		this.l = l;
		l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 1, 1);
	}

	@Override
	public String getSaveFileName() {
		return null;
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
		l.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, l, 100, 0, 0, 0, 0.5);
		l.getWorld().spawnParticle(Particle.PORTAL, l, 100, 0, 0, 0, 0.5);
	}

	@Override
	public String getBlockName() {
		return "Magica Crafter";
	}

	@Override
	public ItemStack getCraftedItem() {
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
