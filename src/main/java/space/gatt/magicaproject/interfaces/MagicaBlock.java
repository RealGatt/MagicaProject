package space.gatt.magicaproject.interfaces;

import com.google.gson.JsonObject;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.TileEntity;
import net.minecraft.server.v1_11_R1.TileEntityMobSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemStack;

public class MagicaBlock {

	private Location location;
	private boolean isActive;
	private ItemStack displayedItem;

	public MagicaBlock(Location location){
		this.location = location;
		this.displayedItem = new ItemStack(Material.DIAMOND_HOE);
		updateBlock();
	}

	public MagicaBlock(JsonObject jsonObject){

	}

	public MagicaBlock(Location location, ItemStack itemStack) {
		this.location = location;
		this.displayedItem = itemStack;
	}

	private void updateBlock(){
		ItemStack itemStack = displayedItem;
		location.getBlock().setType(Material.MOB_SPAWNER);

		CraftWorld ws = (CraftWorld)location.getWorld(); //W is your normal bukkit world . . . I'm using player.getWorld()
		NBTTagCompound ntc = null;
		TileEntityMobSpawner te = (TileEntityMobSpawner)ws.getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		if (te != null)
		{
			ntc = new NBTTagCompound();
			ntc.setString("RequiredPlayerRange", "0s");
			ntc.getCompound("SpawnData").setString("id", "minecraft:armor_stand");
			ntc.getCompound("SpawnData").setString("Invisible", "1");
			ntc.getCompound("SpawnData").setString("Marker", "1");
			ntc.getCompound("SpawnData").getList("ArmorItems", 4).get(3).setString("id", "minecraft:" + itemStack.getType().name().toLowerCase());
			ntc.getCompound("SpawnData").getList("ArmorItems", 4).get(3).setString("Count", "1b");
			ntc.getCompound("SpawnData").getList("ArmorItems", 4).get(3).setString("id", "Damage:" + itemStack.getDurability() + "s");
			ntc.getCompound("SpawnData").getList("ArmorItems", 4).get(3).getCompound("tag").setString("Unbreakable", "1");
			te.save(ntc);
		}
	}

	public void setLocation(Location location) {
		this.location = location;
		updateBlock();
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public void setDisplayedItem(ItemStack displayedItem) {
		updateBlock();
	}

	public Location getLocation(){
		return null;
	}



	public void runParticles(){}

	public boolean isActive(){return false;}

}
