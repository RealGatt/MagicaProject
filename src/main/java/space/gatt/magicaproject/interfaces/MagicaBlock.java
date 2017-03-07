package space.gatt.magicaproject.interfaces;

import com.google.gson.JsonObject;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.TileEntityMobSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;

public class MagicaBlock {

	private Location location;
	private boolean isActive;
	private ItemStack displayedItem;
	private boolean displayedInSpawner = true;

	public static MagicaBlock getMagicaBlockAtLocation(Block b){
		return getMagicaBlockAtLocation(b.getLocation());
	}

	public static MagicaBlock getMagicaBlockAtLocation(Location l){
		if (l.getBlock().hasMetadata("MagicaObject")){
			return l.getBlock().getMetadata("MagicaObject").get(0).value() instanceof MagicaBlock ? (MagicaBlock)l.getBlock().getMetadata("MagicaObject").get(0).value():null;
		}
		return null;
	}

	public MagicaBlock(Location location){
		this.location = location;
		this.displayedItem = new ItemStack(Material.DIAMOND_HOE);
	}

	public MagicaBlock(JsonObject jsonObject){

	}

	public MagicaBlock(Location location, ItemStack itemStack) {
		this.location = location;
		this.displayedItem = itemStack;
	}

	public boolean isDisplayedInSpawner() {
		return displayedInSpawner;
	}

	public void setDisplayedInSpawner(boolean displayedInSpawner) {
		this.displayedInSpawner = displayedInSpawner;
	}

	public void updateBlock(){
		if (isDisplayedInSpawner()) {
			ItemStack itemStack = displayedItem;
			if (displayedItem == null) {
				return;
			}
			location.getBlock().setType(Material.MOB_SPAWNER);

			CraftWorld ws = (CraftWorld) location.getWorld();
			NBTTagCompound ntc = null;
			TileEntityMobSpawner te = (TileEntityMobSpawner) ws.getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			if (te != null) {
				ntc = te.d(); // Adding specific entities to the Mob Spawner. In this case; an inviisble armor stand with a modelled Diamond Hoe. All of this was trial and error, feel free to copy if needed.
				ntc.setShort("RequiredPlayerRange", (short) 0);
				ntc.getCompound("SpawnData").setString("id", "minecraft:armor_stand");
				ntc.getCompound("SpawnData").setShort("Invisible", (short) 1);
				ntc.getCompound("SpawnData").setShort("Marker", (short) 1);
				NBTTagList list = ntc.getCompound("SpawnData").getList("ArmorItems", 4);
				list.add(list.get(0));
				list.add(list.get(1));
				list.add(list.get(2));
				list.add(list.get(3));
				list.get(3).setString("id", "minecraft:" + itemStack.getType().name().toLowerCase());
				list.get(3).setShort("Count", (short) 1);
				list.get(3).setShort("Damage", itemStack.getDurability());
				NBTTagCompound tag = new NBTTagCompound();
				tag.setShort("Unbreakable", (short) 1);
				list.get(3).set("tag", tag);
				ntc.getCompound("SpawnData").set("ArmorItems", list);
				te.a(ntc);
				te.save(ntc);
				te.update();
			}
		}
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public void setDisplayedItem(ItemStack displayedItem) {
		this.displayedItem = displayedItem;
	}


	public Location getLocation(){
		return location;
	}



	public void runParticles(){}

	public boolean isActive(){return isActive;}

}
