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
import space.gatt.magicaproject.CancellableBukkitTask;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.ItemProjectileLandEvent;
import space.gatt.magicaproject.extra.ItemProjectile;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.extra.ParticleTrail;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.wand.WandCore;
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.BlockLooping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrbOfHarvest implements Craftable{


	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {

			List<Material> always = Arrays.asList(Material.LOG, Material.LOG_2,
					Material.LEAVES, Material.LEAVES_2,
					Material.RED_ROSE, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER,
					Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.HUGE_MUSHROOM_2, Material.HUGE_MUSHROOM_1);

			ItemStack[] plants = new ItemStack[]{

					new ItemStack(Material.CROPS, 1, (byte)7),
					new ItemStack(Material.POTATO, 1, (byte)7),
					new ItemStack(Material.CARROT, 1, (byte)7),
					new ItemStack(Material.BEETROOT_BLOCK, 1, (byte)3),

			};

			Random rnd = new Random();

			private void markForHarvest(final Block b, Location l){
				int time = (rnd.nextInt(100) / 2 * 20) / 10;
				final Material type = b.getType();
				CancellableBukkitTask task = new CancellableBukkitTask() {
					private int timeTaken = 0;
					@Override
					public void run() {
						timeTaken++;

						if (rnd.nextBoolean()) {
							b.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
									b.getLocation().clone().add(0.5, 0.5, 0.5), 1, 0.3, 0.3, 0.3, 0);
						}
						if (b.getType() != type){
							cancel();
						}
						if ((time - timeTaken) <= 1){
							for (ItemStack drop : b.getDrops()){
								l.getWorld().dropItemNaturally(l, drop);
							}
							b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
							b.setType(Material.AIR);
							cancel();
							return;
						}
					}
				};
				task.setTaskId(Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), task
				, 1, 10).getTaskId());
			}

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
					BaseUtils.addCooldownToItem(e.getItem(), e.getPlayer(), 20);
					ItemProjectile pro = new ItemProjectile(item, e.getPlayer().getEyeLocation(), "orbofharvest").
							shoot(e.getPlayer().getEyeLocation().getDirection().multiply(1).add(new Vector(0, 0.2, 0)));
					new ParticleTrail(pro.getEntity(), Particle.VILLAGER_HAPPY, 0).startTrail();
				}
			}

			@EventHandler
			public void onItemLand(ItemProjectileLandEvent e){
				if (e.getProjectile().getData().equalsIgnoreCase("orbofharvest")){
					e.removeEntity();
					ArrayList<Block> blocks = BlockLooping.loopSphere(e.getLocation().clone().add(0, 1, 0), 15, true);
					for (Block b : blocks){
						if (always.contains(b.getType())){
							markForHarvest(b, e.getLocation());
						}else {
							for (ItemStack is : plants) {
								if (b.getType() == is.getType() && b.getData() == is.getData().getData()) {
									markForHarvest(b, e.getLocation());
								}
							}
						}
					}
				}
			}

		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack corruptionOrb = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta)corruptionOrb.getItemMeta();
		im.setEffect(FireworkEffect.builder().withColor(Color.GREEN).build());
		im.setDisplayName(BaseUtils.colorString("&aOrb of Harvest"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Throw me to harvest an area"), MagicaMain.getLoreLine().get(0)));
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
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(
				Arrays.asList(new ItemStack(Material.IRON_AXE),
						WandCore.getWoodCore(),
						new ItemStack(Material.WATER_BUCKET))),
				getStaticCraftedItem(), 1000, 10, true);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

}
