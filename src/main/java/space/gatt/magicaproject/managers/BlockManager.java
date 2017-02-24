package space.gatt.magicaproject.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.EntityBlock;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.utilities.BaseUtils;

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
			if (mb instanceof EntityBlock){
				((EntityBlock)mb).spawnExtra();
			}
		}
		mb.getLocation().getBlock().setMetadata("IsMagicaBlock", new FixedMetadataValue(MagicaMain.getMagicaMain(), true));
	}

	public void removeBlock(MagicaBlock mb) {
		if (runningBlocks.contains(mb)) {
			runningBlocks.remove(mb);
		}
		if (mb instanceof EntityBlock){
			((EntityBlock)mb).destroyExtra();
		}
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
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasItem()){
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
					e.setCancelled(true);
					try {
						itemToClass.get(e.getItem()).getConstructor(new Class[]{Location.class}).newInstance(b.getLocation());
					}catch (Exception exp){
						exp.printStackTrace();
					}
				}
			}
		}
	}
}
