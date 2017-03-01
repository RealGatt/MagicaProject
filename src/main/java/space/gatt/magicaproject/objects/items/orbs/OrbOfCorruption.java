package space.gatt.magicaproject.objects.items.orbs;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.util.Vector;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.ItemProjectile;
import space.gatt.magicaproject.extra.ItemProjectileLandEvent;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.BlockLooping;

import java.util.ArrayList;
import java.util.Arrays;

public class OrbOfCorruption implements Craftable{


	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onRightClick(PlayerInteractEvent e){
				if (e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
						BaseUtils.matchItem(e.getItem(), getStaticCraftedItem())){
					ItemStack item = e.getItem().clone();
					item.setAmount(1);
					if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
						e.getItem().setAmount(e.getItem().getAmount() - 1);
						if (e.getItem().getAmount() <= 0){
							if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(e.getItem())){
								e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							}else if (e.getPlayer().getInventory().getItemInOffHand().isSimilar(e.getItem())){
								e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
							}
						}
					}
					new ItemProjectile(item, e.getPlayer().getEyeLocation(), "OrbOfDestruction").
							shoot(e.getPlayer().getEyeLocation().getDirection().multiply(1).add(new Vector(0, 0.4, 0)));
				}
			}

			@EventHandler
			public void onItemLand(ItemProjectileLandEvent e){
				if (e.getProjectile().getData().equalsIgnoreCase("orbofdestruction")){
					e.removeEntity();
					e.getLocation().getWorld().playSound(e.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 1, 1);
					ArrayList<Block> blocks = BlockLooping.loopSphere(e.getLocation().clone().add(0, 1, 0), 8, true);
					Material[] natural = new Material[]{Material.GRASS, Material.DIRT, Material.MYCEL,
														Material.RED_MUSHROOM, Material.RED_ROSE, Material.YELLOW_FLOWER,
														Material.}
					for (Block b : blocks){

					}
				}
			}

		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack corruptionOrb = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta)corruptionOrb.getItemMeta();
		im.setEffect(FireworkEffect.builder().withColor(Color.BLACK).build());
		im.setDisplayName(BaseUtils.colorString("&0Orb of Corruption"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Throw me to corrupt an area"), MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		corruptionOrb.setItemMeta(im);
		return corruptionOrb;
	}

	@Override
	public String getItemName() {
		return "Orb of Corruption";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.NETHER_WART_BLOCK),
						new ItemStack(Material.EYE_OF_ENDER),
						new ItemStack(Material.FERMENTED_SPIDER_EYE),
						new ItemStack(Material.BEETROOT_SEEDS),
						new ItemStack(Material.ROTTEN_FLESH))),
				getStaticCraftedItem(), 1000, 10, true);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

}
