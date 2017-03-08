package space.gatt.magicaproject.extra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.interfaces.MagicaBlock;

public class BlockDisplayName {


	private String display;
	private MagicaBlock block;
	private long updateTime;

	private BukkitTask task;

	private ArmorStand stand;


	public BlockDisplayName(MagicaBlock block, String name, long updatetime){
		this.display = name;
		this.block = block;
		this.updateTime = updatetime;
		stand = block.getLocation().clone().getWorld().spawn(block.getLocation().clone().add(0.5, .75, 0.5), ArmorStand.class);
		stand.setMarker(true);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setGravity(false);
		stand.setCustomNameVisible(true);
		stand.setCustomName(cc(name));
		startTask();
	}

	public void destroy(){
		this.stand.remove();
		this.task.cancel();
	}

	private void startTask(){
		this.task = Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(),()->{
			if (display.equalsIgnoreCase("")) {
				stand.setCustomNameVisible(false);
			} else {
				stand.setCustomNameVisible(true);
			}
			stand.setCustomName(cc(display));
		}, updateTime, updateTime);
	}

	public void restartTask(){
		this.task.cancel();
		startTask();
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public MagicaBlock getBlock() {
		return block;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}


	private String cc(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
