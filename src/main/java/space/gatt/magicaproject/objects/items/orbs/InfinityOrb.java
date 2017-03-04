package space.gatt.magicaproject.objects.items.orbs;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import space.gatt.magicaproject.CancellableBukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.ItemProjectileLandEvent;
import space.gatt.magicaproject.extra.ItemProjectile;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.extra.ParticleTrail;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.BlockLooping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InfinityOrb implements Craftable{


	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onRightClick(PlayerInteractEvent e){
				if (e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
						BaseUtils.matchItem(e.getItem(), getStaticCraftedItem())){
					e.setCancelled(true);
				}
			}

		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack corruptionOrb = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta im = corruptionOrb.getItemMeta();
		im.setDisplayName(BaseUtils.colorString("&6&lInfinity Orb"));
		im.addItemFlags(ItemFlag.values());
		im.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
		im.setLore(Arrays.asList(BaseUtils.colorString("&6An orb of infinite energy."), MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		corruptionOrb.setItemMeta(im);
		return corruptionOrb;
	}

	@Override
	public String getItemName() {
		return "Orb of Life";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(OrbOfCorruption.getStaticCraftedItem(),
						OrbOfLife.getStaticCraftedItem())),
				getStaticCraftedItem(), 100000, 10, true);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

}
