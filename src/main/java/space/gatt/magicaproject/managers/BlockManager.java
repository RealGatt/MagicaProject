package space.gatt.magicaproject.managers;

import org.bukkit.Bukkit;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {

	private ArrayList<MagicaBlock> runningBlocks = new ArrayList<>();

	public BlockManager() {
		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), ()->{
			for (MagicaBlock mb : runningBlocks){
				if (mb.isActive()) {
					mb.runParticles();
				}
			}
		}, 10, 10);
	}

	public void registerBlock(MagicaBlock mb){
		if (!runningBlocks.contains(mb)){
			runningBlocks.add(mb);
		}
	}

	public void removeBlock(MagicaBlock mb){
		if (runningBlocks.contains(mb)){
			runningBlocks.remove(mb);
		}
	}

	public void shutdown(){
		for (MagicaBlock mb : runningBlocks){
			if (mb instanceof Saveable && mb.isActive()){
				((Saveable)mb).shutdownCall();
			}
		}
	}
}