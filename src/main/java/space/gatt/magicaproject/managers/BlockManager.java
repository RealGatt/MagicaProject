package space.gatt.magicaproject.managers;

import org.bukkit.Bukkit;
import org.bukkit.metadata.FixedMetadataValue;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.EntityBlock;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;

import java.util.ArrayList;

public class BlockManager {

	private ArrayList<MagicaBlock> runningBlocks = new ArrayList<>();

	public BlockManager() {
		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), () -> {
			for (MagicaBlock mb : runningBlocks) {
				if (mb.isActive()) {
					mb.runParticles();
				}
			}
		}, 1, 1);
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
}
