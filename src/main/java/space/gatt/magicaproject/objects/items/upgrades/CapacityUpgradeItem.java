package space.gatt.magicaproject.objects.items.upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.enums.UpgradeType;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.interfaces.UpgradeItem;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.objects.items.wand.WandCore;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CapacityUpgradeItem implements Craftable, UpgradeItem {

	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onRightClick(PlayerInteractEvent e){
				if (e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
						BaseUtils.matchItem(e.getItem(), getStaticCraftedItem())){
					e.setUseItemInHand(Event.Result.DENY);
					e.setUseInteractedBlock(Event.Result.DENY);
				}
			}

		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack upgrade = MagicaMain.getBaseItem();
		upgrade.setDurability((short)3);
		ItemMeta im = upgrade.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&7Upgrade: &aCapacity"));
		im.addItemFlags(ItemFlag.values());
		im.addEnchant(Enchantment.DURABILITY, 1, false);
		im.setLore(Arrays.asList(BaseUtils.colorString("&7A Capacity Upgrade for certain &9MagicaBlocks."),
				BaseUtils.colorString("&9Right-Click&7 to attempt to apply the upgrade"),
				MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		upgrade.setItemMeta(im);
		return upgrade;
	}

	@Override
	public String getItemName() {
		return "Upgrade: Capacity";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(
				WandCore.getIronCore(),
				WandCore.getIronCore(),
				new ItemStack(Material.CHEST),
				MagicaShard.getStaticCraftedItem())),
				getStaticCraftedItem(), 100, 0, true);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

	@Override
	public UpgradeType getUpgradeType() {
		return UpgradeType.CAPACITY_UPGRADE;
	}


}
