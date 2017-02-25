package space.gatt.magicaproject.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockManager implements Listener{

	private ArrayList<MagicaBlock> runningBlocks = new ArrayList<>();
	private HashMap<ItemStack, Class> itemToClass = new HashMap<>();

	public BlockManager() {

		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());

		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), () -> {
			for (MagicaBlock mb : runningBlocks) {
				if (mb.isActive()) {
					mb.runParticles();
				}
			}
		}, 1, 1);
	}

	public void registerItem(ItemStack item, Class clz){
		this.itemToClass.put(item, clz);
	}

	public void registerBlock(MagicaBlock mb) {
		if (!runningBlocks.contains(mb)) {
			runningBlocks.add(mb);
		}
		mb.getLocation().getBlock().setMetadata("IsMagicaBlock", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
	}

	public void removeBlock(MagicaBlock mb) {
		if (runningBlocks.contains(mb)) {
			runningBlocks.remove(mb);
		}
		mb.getLocation().getWorld().playEffect(mb.getLocation(), Effect.STEP_SOUND, new MaterialData(Material.IRON_BLOCK).getItemTypeId());
		mb.getLocation().getBlock().removeMetadata("IsMagicaBlock", MagicaMain.getMagicaMain());
	}

	public void shutdown() {
		for (MagicaBlock mb : runningBlocks) {
			if (mb instanceof Saveable) {
				MagicaMain.getMagicaMain().getStorageManager().registerSaveable(((Saveable) mb));
			}
		}
	}



	@EventHandler
	public void onPlace(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasItem() && !e.isCancelled()){
			BlockFace face = e.getBlockFace();
			Block b = e.getClickedBlock().getRelative(face);
			if (b.isEmpty() || b.isLiquid()){
				boolean isItem = false;
				for (ItemStack i : itemToClass.keySet()){
					if (BaseUtils.matchItem(i, e.getItem())){
						isItem = true;
						break;
					}
				}
				if (isItem){
					e.setUseItemInHand(Event.Result.DENY);
					e.setUseInteractedBlock(Event.Result.DENY);
					boolean placed = false;
					ItemStack itemCopy = e.getItem().clone();
					itemCopy.setAmount(1);
					try {
						placeCheck : for (Constructor m : itemToClass.get(itemCopy).getConstructors()){
							if (m.getParameterCount() == 1){
								for (Class typeParameter : m.getParameterTypes()){
									if (typeParameter == Location.class){
										m.newInstance(b.getLocation());
										placed = true;
										break placeCheck;
									}else if (typeParameter == OfflinePlayer.class){
										placed = true;
										m.newInstance((OfflinePlayer)e.getPlayer());
										break placeCheck;
									}
								}
							}
							if (m.getParameterCount() == 2){
								int correct = 0;
								for (Class typeParameter : m.getParameterTypes()){
									if (typeParameter == Location.class){
										correct++;
									}else if (typeParameter == OfflinePlayer.class){
										correct++;
									}
									if (correct == 2){
										placed = true;
										m.newInstance(b.getLocation(), (OfflinePlayer)e.getPlayer());
										break placeCheck;
									}
								}
							}
						}
						if (placed) {
							b.getWorld().playSound(b.getLocation(), Sound.BLOCK_METAL_PLACE, 1, 1);
							if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
								if (e.getItem().getAmount() > 1) {
									e.getItem().setAmount(e.getItem().getAmount() - 1);
								} else {
									e.getItem().setAmount(0);
									if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(e.getItem())){
										e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
									}else if (e.getPlayer().getInventory().getItemInOffHand().isSimilar(e.getItem())){
										e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
									}
								}
							}
						}
					}catch (Exception exp){
						exp.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (!e.isCancelled()) {
			if (e.getClickedInventory().getType() == InventoryType.CREATIVE
					|| e.getClickedInventory().getType() == InventoryType.PLAYER
					|| e.getClickedInventory().getType() == InventoryType.ENDER_CHEST
					|| e.getClickedInventory().getType() == InventoryType.CHEST) {
				if (e.getClick() == ClickType.LEFT) {
					if (e.getCursor() != null && e.getCurrentItem() != null) {
						if (BaseUtils.matchItem(e.getCurrentItem(), e.getCursor())
								&& e.getCurrentItem().getMaxStackSize() == 1
								&& e.getCurrentItem().hasItemMeta()
								&& e.getCurrentItem().getItemMeta().hasLore()) {
							e.setCancelled(true);
							int total = e.getCurrentItem().getAmount() + e.getCursor().getAmount();
							if (total > 64) {
								e.getCurrentItem().setAmount(64);
								total -= 64;
								e.getCursor().setAmount(total);
								return;
							} else {
								e.getCurrentItem().setAmount(total);
								e.setCursor(new ItemStack(Material.AIR));
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop(ItemSpawnEvent e){
		if (e.getEntity().getItemStack().hasItemMeta() &&
				e.getEntity().getItemStack().getItemMeta().hasLore() &&
				e.getEntity().getItemStack().getItemMeta().getLore().contains(MagicaMain.getLoreLine().get(0))
				&& e.getEntity().getItemStack().getMaxStackSize() == 1){
			for (Entity ent : e.getEntity().getNearbyEntities(3, 2, 3)){
				if (ent instanceof Item){
					Item i = (Item)ent;
					if (BaseUtils.matchItem(i.getItemStack(), e.getEntity().getItemStack()) && i.getCustomName() == null && e.getEntity().getCustomName() == null){
						if (i.getItemStack().getAmount() + e.getEntity().getItemStack().getAmount() <= 64){
							e.getEntity().getItemStack().setAmount(e.getEntity().getItemStack().getAmount() + i.getItemStack().getAmount());
							i.remove();
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if (e.getItem().getItemStack().hasItemMeta() &&

				e.getItem().getItemStack().getItemMeta().getLore().contains(MagicaMain.getLoreLine().get(0))){
			for (int amount = e.getItem().getItemStack().getAmount(); amount >= 0; amount--) {
				if (!e.getItem().isDead() && e.getItem().getItemStack() != null && e.getItem() != null) {
					itemcheck:
					for (ItemStack is : e.getPlayer().getInventory()) {
						if (BaseUtils.matchItem(is, e.getItem().getItemStack()) && is.getAmount() < 64 && is.getMaxStackSize() == 1) {
							e.getItem().getItemStack().setAmount(e.getItem().getItemStack().getAmount() - 1);
							if (e.getItem().getItemStack().getAmount() == 0) {
								e.getItem().remove();
							}
							is.setAmount(is.getAmount() + 1);
							e.setCancelled(true);
							break itemcheck;
						}
					}
				}
			}
		}
	}
}
