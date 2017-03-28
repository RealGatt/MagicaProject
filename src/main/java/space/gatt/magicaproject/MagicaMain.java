package space.gatt.magicaproject;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.MagicaBlock;
import space.gatt.magicaproject.interfaces.Saveable;
import space.gatt.magicaproject.managers.BlockManager;
import space.gatt.magicaproject.managers.ManaManager;
import space.gatt.magicaproject.managers.RecipeManager;
import space.gatt.magicaproject.managers.StorageManager;
import space.gatt.magicaproject.objects.blocks.MagicCrafter;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MagicaMain extends JavaPlugin implements Listener{

	private static MagicaMain magicaMain;
	private StorageManager storageManager;
	private MagicaMain plugin;
	private ManaManager manaManager;
	private BlockManager blockManager;
	private RecipeManager recipeManager;
	private String resourcePack = "https://dev.gatt.space/resources/MagicaProject.zip";

	public static MagicaMain getMagicaMain() {
		return magicaMain;
	}

	@Override
	public void onDisable() {
		getManaManager().shutdownCall();
		getBlockManager().shutdown();
		// StorageManager should be last!!
		storageManager.shutdown();
	}

	@Override
	public void onEnable() {
		magicaMain = this;
		plugin = this;
		manaManager = new ManaManager();
		blockManager = new BlockManager();
		storageManager = new StorageManager();
		recipeManager = new RecipeManager();

		ShapedRecipe magicCrafterRecipe = new ShapedRecipe(MagicCrafter.getStaticCraftedItem());
		magicCrafterRecipe.shape("ABA", "FWR", "IOI");
		magicCrafterRecipe.setIngredient('A', Material.AIR);
		magicCrafterRecipe.setIngredient('B', Material.ENCHANTED_BOOK);
		magicCrafterRecipe.setIngredient('F', Material.YELLOW_FLOWER);
		magicCrafterRecipe.setIngredient('R', Material.RED_ROSE);
		magicCrafterRecipe.setIngredient('I', Material.DIAMOND);
		magicCrafterRecipe.setIngredient('W', Material.WORKBENCH);
		magicCrafterRecipe.setIngredient('O', Material.OBSIDIAN);
		ShapelessRecipe magicCrafterRecipe2 = new ShapelessRecipe(MagicCrafter.getStaticCraftedItem());
		magicCrafterRecipe2.addIngredient(1, Material.BEDROCK);
		Bukkit.addRecipe(magicCrafterRecipe2);
		Bukkit.addRecipe(magicCrafterRecipe);

		Bukkit.getPluginManager().registerEvents(this, this);

		Reflections reflections = new Reflections("space.gatt.magicaproject");

		Set<Class<? extends Craftable>> subTypes = reflections.getSubTypesOf(Craftable.class);
		System.out.println("Found " + subTypes.size() + " Craftable Items.");
		for (Class c : subTypes) {
			try {
				for (Method m : c.getMethods()){
					if (m.getReturnType() == ArrayList.class && Modifier.isStatic(m.getModifiers())){
						ArrayList<MagicaRecipe> recipes = (ArrayList<MagicaRecipe>)m.invoke(this);
						for (MagicaRecipe recipe : recipes){
							recipeManager.registerRecipe(recipe);
						}
						System.out.println("Registered recipes for " + c.getSimpleName());
					}


					if (Modifier.isStatic(m.getModifiers()) && m.getName().equalsIgnoreCase("registerItemListener")){
						m.invoke(this);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Set<Class<? extends Saveable>> saveable = reflections.getSubTypesOf(Saveable.class);
		System.out.println("Found " + saveable.size() + " Saveable Objects.");
		for (Class c : saveable) {
			try {
				for (Method m : c.getMethods()){
					if (m.getReturnType() == String.class && Modifier.isStatic(m.getModifiers()) && m.getName().toLowerCase().contains("filename")){
						String fileName = (String)m.invoke(this);
						getStorageManager().loadFromFile(fileName, c);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Set<Class<? extends MagicaBlock>> magicaBlocks = reflections.getSubTypesOf(MagicaBlock.class);
		System.out.println("Found " + magicaBlocks.size() + " MagicaBlocks.");
		for (Class c : magicaBlocks) {
			try {
				for (Method m : c.getMethods()){
					if (m.getName().equalsIgnoreCase("registerListener")){
						m.invoke(this);
						System.out.println("Registered listeners for " + c.getSimpleName());
					}

					if (m.getReturnType() == ItemStack.class){
						ItemStack craftedItem = (ItemStack)m.invoke(this);
						blockManager.registerItem(craftedItem, c);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getLoreLine(){
		return Arrays.asList(BaseUtils.colorString("&9MagicaProject"));
	}

	public static ItemStack getCustomItem(Material mat){
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		speed.set("Amount", new NBTTagDouble(0.1));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(894654));
		speed.set("UUIDMost", new NBTTagInt(2872));
		speed.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(speed);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		is = CraftItemStack.asBukkitCopy(nmsStack);
		return is;
	}

	public static ItemStack getCustomItem(Material mat, short dmg){
		ItemStack is = getCustomItem(mat);
		is.setDurability(dmg);
		return is;
	}

	public static ItemStack getBaseBlockStack(){
		ItemStack is = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		speed.set("Amount", new NBTTagDouble(0.1));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(894654));
		speed.set("UUIDMost", new NBTTagInt(2872));
		speed.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(speed);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		is = CraftItemStack.asBukkitCopy(nmsStack);
		return is;
	}

	public static ItemStack getBaseGUI(){
		ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		speed.set("Amount", new NBTTagDouble(0.1));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(894654));
		speed.set("UUIDMost", new NBTTagInt(2872));
		speed.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(speed);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		is = CraftItemStack.asBukkitCopy(nmsStack);
		return is;
	}

	public static ItemStack getBaseItem(){
		ItemStack is = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		speed.set("Amount", new NBTTagDouble(0.1));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(894654));
		speed.set("UUIDMost", new NBTTagInt(2872));
		speed.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(speed);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		is = CraftItemStack.asBukkitCopy(nmsStack);
		return is;
	}

	public static ItemStack getBaseBlockStack(short damage){
		ItemStack is = getBaseBlockStack();
		is.setDurability(damage);
		return is;
	}

	public static ItemStack getBaseGUI(short damage){
		ItemStack is = getBaseGUI();
		is.setDurability(damage);
		return is;
	}

	public static ItemStack getBaseItem(short damage){
		ItemStack is = getBaseItem();
		is.setDurability(damage);
		return is;
	}

	public static ItemStack getBaseItemStackable(){
		ItemStack is = new ItemStack(Material.DIAMOND_SPADE);
		ItemMeta im = is.getItemMeta();
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		speed.set("Amount", new NBTTagDouble(0.1));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(894654));
		speed.set("UUIDMost", new NBTTagInt(2872));
		speed.set("Slot", new NBTTagString("mainhand"));
		modifiers.add(speed);
		compound.set("AttributeModifiers", modifiers);
		nmsStack.setTag(compound);
		is = CraftItemStack.asBukkitCopy(nmsStack);
		return is;
	}

	public static ItemStack getBaseItemStackable(short damage){
		ItemStack is = getBaseItemStackable();
		is.setDurability(damage);
		return is;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		e.getPlayer().setResourcePack(resourcePack);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (e.getInventory().getHolder() == null || (e.getClickedInventory() != null && e.getClickedInventory().getHolder() == null)){
			if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(BaseUtils.colorString("&cShift-Clicking in custom inventories likes to throw Spigot-Based errors, so it's been disabled~"));
			}
		}
	}

	@EventHandler
	public void onHopperPickup(InventoryPickupItemEvent e){
		e.setCancelled(e.getItem().getCustomName() != null);
		if (e.getInventory().getType() == InventoryType.HOPPER && e.getItem().getCustomName() == null){
			Block blockAbove = e.getInventory().getLocation().clone().add(0, 1, 0).getBlock();
			if (blockAbove.hasMetadata("IsMagicaBlock")){
				if (blockAbove.getMetadata("IsMagicaBlock").get(0).asBoolean()){
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e){
		if (e.getEntity().getCustomName() != null){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onCraft(CraftItemEvent e){
		for (ItemStack is : e.getInventory().getMatrix()){
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length
						&& im.hasLore() &&
						im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))) {
					e.setCancelled(true);
					e.setResult(Event.Result.DENY);
					e.setCurrentItem(new ItemStack(Material.AIR));
				}
			}
		}
	}

	@EventHandler
	public void onFurnace(FurnaceBurnEvent e){
		ItemMeta im = e.getFuel().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
			e.setBurning(false);
			e.setBurnTime(0);
		}
	}

	@EventHandler
	public void onFurnace(FurnaceSmeltEvent e){
		ItemMeta im = e.getSource().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
		}
		im = e.getResult().getItemMeta();
		if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onAnvil(final PrepareAnvilEvent e){
		e.getInventory().forEach(new Consumer<ItemStack>() {
			@Override
			public void accept(ItemStack stack) {
				if (stack != null && stack.hasItemMeta()) {
					ItemMeta im = stack.getItemMeta();
					if (im.isUnbreakable() && im.getItemFlags().size() == ItemFlag.values().length && im.getLore().contains(BaseUtils.colorString("&9MagicaProject"))) {
						e.setResult(new ItemStack(Material.AIR));
					}
				}
			}
		});
	}

	public BlockManager getBlockManager() {
		return blockManager;
	}

	public ManaManager getManaManager() {
		return manaManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}
}
