package space.gatt.magicaproject.interfaces;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.TileEntityMobSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;

import java.util.HashMap;
import java.util.Set;

public class MagicaBlock implements Listener{

	private Location location;
	private boolean isActive;
	private ItemStack displayedItem;
	private boolean displayedInSpawner = true;
	private HashMap<UpgradeType, UpgradeInstance> appliedUpgrades = new HashMap<>();

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
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
	}

	public MagicaBlock(JsonObject jsonObject){
		if (jsonObject.has("upgrades")){
			JsonObject upObjs = jsonObject.get("upgrades").getAsJsonObject();
			for (UpgradeType upd : UpgradeType.values()){
				if (upObjs.has(upd.name())){
					if (acceptsUpgrade(upd)){

						for (int cnt = 0; cnt < upObjs.get(upd.name()).getAsInt(); cnt++){
							applyUpgrade(upd);
						}
					}
				}
			}
		}
	}

	public MagicaBlock(Location location, ItemStack itemStack) {
		this.location = location;
		this.displayedItem = itemStack;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Bukkit.broadcastMessage("MagicaBlock test");
		if (isActive()) {

			if (e.getBlock().getLocation().equals(getLocation())) {
				for (UpgradeInstance upgrades : getUpgrades()){
					ItemStack drop = upgrades.getType().getUpgradeItem().clone();
					drop.setAmount(upgrades.getLevel());
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
				}
			}
		}
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
		return location.clone();
	}



	public void runParticles(){}

	public boolean isActive(){return isActive;}

	public boolean acceptsUpgrade(UpgradeType type){
		return false;
	}

	public void applyUpgrade(UpgradeType upgrade){
		UpgradeInstance inst;
		if (appliedUpgrades.containsKey(upgrade)){
			inst = appliedUpgrades.get(upgrade);
			inst.increaseLevel(1);
		}else{
			inst = upgrade.getNewInstance();
			if (inst != null) {
				inst.setLevel(1);
			}else{
				Bukkit.broadcastMessage("Unable to create new instance of " + upgrade.name());
				return;
			}
			appliedUpgrades.put(upgrade, inst);
		}
	}

	public Set<UpgradeType> getAppliedUpgrades() {
		return appliedUpgrades.keySet();
	}

	public UpgradeInstance[] getUpgrades() {
		UpgradeInstance[] upgrades = appliedUpgrades.values().toArray(new UpgradeInstance[appliedUpgrades.values().size()]);
		return upgrades;
	}

	public UpgradeInstance getUpgrade(UpgradeType type){
		return appliedUpgrades.containsKey(type) ? appliedUpgrades.get(type) : null;
	}

	public JsonObject saveUpgrades(){
		Gson gson = new Gson();
		HashMap<String, Integer> upgradesHash = new HashMap<>();
		for (UpgradeInstance upg : getUpgrades()){
			upgradesHash.put(upg.getType().name(), upg.getLevel());
		}
		return gson.toJsonTree(upgradesHash).getAsJsonObject();
	}

}
