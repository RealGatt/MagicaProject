package space.gatt.magicaproject.managers;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import space.gatt.magicaproject.CancellableBukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.ManaStorable;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
public class BlockManager implements Listener{

	private HashMap<String, MagicaBlock> runningBlocks = new HashMap<>();
	private HashMap<ItemStack, Class> itemToClass = new HashMap<>();

	public BlockManager() {

		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());

		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), () -> {
			for (MagicaBlock mb : runningBlocks.values()) {
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
		runningBlocks.put(mb.getLocation().toString(), mb);
		mb.setActive(true);
		if (mb instanceof ManaStorable){
			mb.getLocation().getBlock().setMetadata("isManaStorage", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
		}
		mb.getLocation().getBlock().setMetadata("IsMagicaBlock", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
		mb.getLocation().getBlock().setMetadata("MagicaObject", new FixedMetadataValue(MagicaMain.getMagicaMain(), mb));
	}

	public void removeBlock(MagicaBlock mb) {
		mb.setActive(false);
		mb.getLocation().getWorld().playEffect(mb.getLocation(), Effect.STEP_SOUND, new MaterialData(Material.IRON_BLOCK).getItemTypeId());
		mb.getLocation().getBlock().removeMetadata("IsMagicaBlock", MagicaMain.getMagicaMain());
		mb.getLocation().getBlock().removeMetadata("isManaStorage", MagicaMain.getMagicaMain());
		mb.getLocation().getBlock().removeMetadata("MagicaObject", MagicaMain.getMagicaMain());
	}

	public void shutdown() {
		for (MagicaBlock mb : runningBlocks.values()) {
			if (mb instanceof Saveable) {
				MagicaMain.getMagicaMain().getStorageManager().registerSaveable(((Saveable) mb));
			}
		}
	}

	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK &&
				e.hasItem() && e.getItem().getType() == MagicaMain.getBaseBlockStack().getType()) {
			BlockFace face = e.getBlockFace();
			Block b = e.getClickedBlock().getRelative(face);
			if (b.isEmpty() || b.isLiquid() && !e.isCancelled()) {
				boolean isItem = false;
				for (ItemStack i : itemToClass.keySet()) {
					if (BaseUtils.matchItem(i, e.getItem())) {
						isItem = true;
						break;
					}
				}
				if (isItem) {
					e.setUseItemInHand(Event.Result.DENY);
					e.setUseInteractedBlock(Event.Result.DENY);
					boolean placed = false;
					ItemStack itemCopy = e.getItem().clone();
					itemCopy.setAmount(1);
					try {
						placeCheck:
						for (Constructor m : itemToClass.get(itemCopy).getConstructors()) {
							if (m.getParameterCount() == 1) {
								for (Class typeParameter : m.getParameterTypes()) {
									if (typeParameter == Location.class) {
										m.newInstance(b.getLocation());
										placed = true;
										break placeCheck;
									} else if (typeParameter == OfflinePlayer.class) {
										placed = true;
										m.newInstance((OfflinePlayer) e.getPlayer());
										break placeCheck;
									}
								}
							}
							if (m.getParameterCount() == 2) {
								int correct = 0;
								for (Class typeParameter : m.getParameterTypes()) {
									if (typeParameter == Location.class) {
										correct++;
									} else if (typeParameter == OfflinePlayer.class) {
										correct++;
									}
									if (correct == 2) {
										placed = true;
										m.newInstance(b.getLocation(), (OfflinePlayer) e.getPlayer());
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
									if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(e.getItem())) {
										e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
									} else if (e.getPlayer().getInventory().getItemInOffHand().isSimilar(e.getItem())) {
										e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
									}
								}
							}
						}
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlaceUpgrade(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasItem() && e.useItemInHand() == Event.Result.ALLOW){
			if (e.getItem().getType() == MagicaMain.getBaseItem().getType()){
				MagicaBlock mb = MagicaBlock.getMagicaBlockAtLocation(e.getClickedBlock().getLocation());
				if (mb != null){
					for (UpgradeType upgrades : UpgradeType.values()){
						if (BaseUtils.matchItem(upgrades.getUpgradeItem(), e.getItem())){
							e.setUseInteractedBlock(Event.Result.DENY);
							if (mb.acceptsUpgrade(upgrades)){
								mb.applyUpgrade(upgrades);
								e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
								e.getPlayer().sendMessage(BaseUtils.colorString("&aUpgrade &6" +
										WordUtils.capitalizeFully(upgrades.name().toLowerCase().replaceAll("_", " ")) + "&a added!" +
										"\n &cUpgrade Level: &b" + mb.getUpgrade(upgrades).getLevel() + "&c!"));
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
								return;
							}else{
								e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
								e.getPlayer().sendMessage(BaseUtils.colorString("&cThis upgrade cannot be applied to this block!"));
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (!e.isCancelled()) {
			if (e.getClickedInventory() != null &&
					(e.getClickedInventory().getType() == InventoryType.CREATIVE
					|| e.getClickedInventory().getType() == InventoryType.PLAYER
					|| e.getClickedInventory().getType() == InventoryType.ENDER_CHEST
					|| e.getClickedInventory().getType() == InventoryType.CHEST)) {
				if (e.getClick() == ClickType.LEFT) {
					if (e.getCursor() != null && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
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
				if (e.getClick() == ClickType.MIDDLE && e.getWhoClicked().getGameMode() == GameMode.CREATIVE){
					if (e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().contains(MagicaMain.getLoreLine().get(0))){
						ItemStack copy = e.getCurrentItem().clone();
						copy.setAmount(64);
						e.setCurrentItem(copy);
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
				&& e.getEntity().getItemStack().getMaxStackSize() == 1
				&& (e.getEntity().getItemStack().getType() == MagicaMain.getBaseBlockStack().getType() ||
				e.getEntity().getItemStack().getType() == MagicaMain.getBaseItemStackable().getType())){
			final CancellableBukkitTask tk = new CancellableBukkitTask() {
				@Override
				public void run() {
					if (e.getEntity().isOnGround()) {
						for (Entity ent : e.getEntity().getNearbyEntities(3, 2, 3)) {
							if (ent instanceof Item) {
								Item i = (Item) ent;
								if (BaseUtils.matchItem(i.getItemStack(), e.getEntity().getItemStack())
										&& i.getCustomName() == null
										&& e.getEntity().getCustomName() == null) {
									if (i.getItemStack().getAmount() + e.getEntity().getItemStack().getAmount() <= 64) {
										e.getEntity().getWorld().playSound(e.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
										i.getItemStack().setAmount(e.getEntity().getItemStack().getAmount()
												+ i.getItemStack().getAmount());
										e.getEntity().remove();
									}
								}
							}
						}
						cancel();
					}
					if (e.getEntity().isDead()){
						cancel();
					}
				}
			};
			tk.setTaskId(Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), tk, 1, 1).getTaskId());

		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if (e.getItem().getItemStack().hasItemMeta() &&
			e.getItem().getItemStack().getItemMeta().hasLore() &&
			e.getItem().getItemStack().getItemMeta().getLore().contains(MagicaMain.getLoreLine().get(0)) &&
				(e.getItem().getItemStack().getType() == MagicaMain.getBaseBlockStack().getType() ||
						e.getItem().getItemStack().getType() == MagicaMain.getBaseItemStackable().getType())){
			for (int amount = e.getItem().getItemStack().getAmount(); amount >= 0; amount--) {
				if (!e.getItem().isDead() && e.getItem().getItemStack() != null && e.getItem() != null) {
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
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
