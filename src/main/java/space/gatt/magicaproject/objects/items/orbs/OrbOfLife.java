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
import space.gatt.magicaproject.utilities.BaseUtils;
import space.gatt.magicaproject.utilities.BlockLooping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrbOfLife implements Craftable{
	private static Listener listener;

	public static Listener getListener() {
		return listener;
	}

	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(listener = new Listener() {

			List<Material> changeFrom = Arrays.asList(Material.STONE, Material.NETHERRACK, Material.SOUL_SAND,
					Material.MYCEL, Material.SAND, Material.GRAVEL);
			ItemStack[] changeTo = new ItemStack[]{
					new ItemStack(Material.GRASS),
					new ItemStack(Material.DIRT, 1, (byte)1)
			};
			ItemStack[] plants = new ItemStack[]{
					new ItemStack(Material.LONG_GRASS, 1, (byte)0),
					new ItemStack(Material.LONG_GRASS, 1, (byte)1),
					new ItemStack(Material.YELLOW_FLOWER),
					new ItemStack(Material.RED_ROSE),
					new ItemStack(Material.RED_ROSE, 1, (byte)1),
					new ItemStack(Material.RED_ROSE, 1, (byte)2),
					new ItemStack(Material.RED_ROSE, 1, (byte)3),
					new ItemStack(Material.RED_ROSE, 1, (byte)4),
					new ItemStack(Material.RED_ROSE, 1, (byte)5),
					new ItemStack(Material.RED_ROSE, 1, (byte)6),
					new ItemStack(Material.RED_ROSE, 1, (byte)7),
					new ItemStack(Material.RED_ROSE, 1, (byte)8),
					new ItemStack(Material.BROWN_MUSHROOM),
					new ItemStack(Material.RED_MUSHROOM)
			};

			Random rnd = new Random();

			private void markForTree(final Block b){
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
							TreeType[] types = new TreeType[]{TreeType.TREE,TreeType.TREE,TreeType.TREE,
									TreeType.BIRCH, TreeType.TALL_BIRCH, TreeType.SMALL_JUNGLE, TreeType.JUNGLE_BUSH, TreeType.SMALL_JUNGLE};
							b.getWorld().generateTree(b.getLocation().add(0, 1, 0), types[rnd.nextInt(types.length - 1)]);
							cancel();
							return;
						}
					}
				};
				task.setTaskId(Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), task
				, 1, 10).getTaskId());
			}

			private void markForChange(Block b, ItemStack is){
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
							b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
							b.setType(is.getType());
							b.setData(is.getData().getData());
							if (b.getRelative(0, 1, 0).getType() == Material.AIR){
								if (rnd.nextBoolean()){
									ItemStack flower = plants[rnd.nextInt(plants.length - 1)];
									b.getRelative(0, 1, 0).setType(flower.getType(), false);
									b.getRelative(0, 1, 0).setData(flower.getData().getData());
								}
							}
							cancel();
							return;
						}
					}
				};
				task.setTaskId(Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), task
						, 1, 10).getTaskId());

			}

			@EventHandler
			private void onRightClick(PlayerInteractEvent e){
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
					ItemProjectile pro = new ItemProjectile(item, e.getPlayer().getEyeLocation(), "orboflife").
							shoot(e.getPlayer().getEyeLocation().getDirection().multiply(1).add(new Vector(0, 0.2, 0)));
					new ParticleTrail(pro.getEntity(), Particle.VILLAGER_HAPPY, 0).startTrail();
				}
			}

			public void orbFunction(Location location){
				ArrayList<Block> blocks = BlockLooping.loopSphere(location.clone().add(0, 1, 0), 10, true);
				for (Block b : blocks){
					if (changeFrom.contains(b.getType())) {
						if (rnd.nextBoolean() && rnd.nextBoolean()){
							markForTree(b);
						}
						markForChange(b, changeTo[rnd.nextInt(changeTo.length - 1)]);
					}
					if (b.getType() == Material.GRASS){
						if (rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean()){
							markForTree(b);
						}else{
							if (rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean()){
								markForChange(b, new ItemStack(Material.GRASS));
							}
						}
					}
				}
			}

			@EventHandler
			private void onItemLand(ItemProjectileLandEvent e){
				if (e.getProjectile().getData().equalsIgnoreCase("orboflife")){
					e.removeEntity();
					orbFunction(e.getLocation());
				}
			}

		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack corruptionOrb = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta)corruptionOrb.getItemMeta();
		im.setEffect(FireworkEffect.builder().withColor(Color.LIME).build());
		im.setDisplayName(BaseUtils.colorString("&aOrb of Life"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(BaseUtils.colorString("&7Throw me to give life to an area"), MagicaMain.getLoreLine().get(0)));
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
				Arrays.asList(new ItemStack(Material.RED_ROSE),
						new ItemStack(Material.YELLOW_FLOWER),
						new ItemStack(Material.SEEDS),
						new ItemStack(Material.WATER_BUCKET),
						new ItemStack(Material.GRASS))),
				getStaticCraftedItem(), 1000, 10, true);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}

}
