package space.gatt.magicaproject.managers;

import org.bukkit.Bukkit;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.objects.MagicCrafter;
import space.gatt.magicaproject.objects.MagicaBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {

	private List<MagicaBlock> runningBlocks = new ArrayList<>();

	public BlockManager() {
		Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), ()->{
			for (MagicaBlock mb : runningBlocks){
				mb.runParticles();
			}
		}, 10, 10);
	}

	public void registerBlock(MagicaBlock mb){
		if (!runningBlocks.contains(mb)){
			runningBlocks.add(mb);
		}
	}

	public void shutdown(){
		for (MagicaBlock mb : runningBlocks){
			if (mb instanceof Saveable){
				((Saveable)mb).shutdownCall();
			}
		}
	}
}
