package space.gatt.magicaproject.objects.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.ItemComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wrench implements Craftable{


	public static void registerItemListener(){

		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			private void onRightClickWrench(PlayerInteractEvent e){
				if (e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK)
						&& BaseUtils.matchItem(e.getItem(), getStaticCraftedItem())){
					if (e.getAction() == Action.RIGHT_CLICK_BLOCK ){
						if (e.getClickedBlock().getType() == Material.GRASS || e.getClickedBlock().getType() == Material.GRASS_PATH){
							e.setUseItemInHand(Event.Result.DENY);
							e.setUseInteractedBlock(Event.Result.DENY);
							return;
						}
					}
					if (e.getClickedBlock().getState() instanceof InventoryHolder) {
						if (e.getClickedBlock().getType() != Material.FURNACE) {
							InventoryHolder holder = ((InventoryHolder) e.getClickedBlock().getState());
							if (holder.getInventory().getContents().length > 0) {
								ItemStack[] items = holder.getInventory().getContents();
								List<ItemStack> itemsSorted = new ArrayList<>();
								for (ItemStack is : items) {
									if (is != null) {
										itemsSorted.add(is);
									}
								}
								itemsSorted.sort(new ItemComparator());
								ItemStack[] sorted = itemsSorted.toArray(new ItemStack[itemsSorted.size() - 1]);
								holder.getInventory().clear();
								holder.getInventory().setContents(sorted);
								e.setCancelled(true);
								e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 2);

							}
						}
					}
				}
			}

		}, MagicaMain.getMagicaMain());

	}

	@Override
	public String getItemName() {
		return "Wrench";
	}

	public static ItemStack getStaticCraftedItem(){
		ItemStack is = MagicaMain.getBaseStack();
		is.setDurability((short)1560);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&bMagic Wrench"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Allows the ability to rotate Magica Pipes"), MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		is.setItemMeta(im);
		return is;
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes() {
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe recipe = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.LEVER)
		)), getStaticCraftedItem(), 100, 0, true);
		recipes.add(recipe);
		return recipes;
	}
	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
