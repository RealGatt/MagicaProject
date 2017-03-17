package space.gatt.magicaproject.objects.items.orbs;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EvokerFangs;
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

public class OrbOfCorruption implements Craftable{

	private static Listener listener;

	public static Listener getListener() {
		return listener;
	}

	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(listener = new Listener() {

			List<Material> natural = Arrays.asList(Material.GRASS, Material.DIRT, Material.GRASS_PATH,
					Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2);
			List<Material> destroy = Arrays.asList(Material.RED_MUSHROOM, Material.RED_ROSE, Material.YELLOW_FLOWER,
					Material.LOG, Material.LOG_2, Material.LONG_GRASS, Material.DEAD_BUSH, Material.LEAVES, Material.LEAVES_2, Material.VINE);

			ItemStack[] changeTo = new ItemStack[]{
					new ItemStack(Material.SOUL_SAND),
					new ItemStack(Material.NETHERRACK),
					new ItemStack(Material.MYCEL),
					new ItemStack(Material.DIRT, 1, (byte)1)
			};

			Random rnd = new Random();

			private void markForDestroy(final Block b){
				int time = (rnd.nextInt(100) / 2 * 20) / 10;
				final Material type = b.getType();
				CancellableBukkitTask task = new CancellableBukkitTask() {
					private int timeTaken = 0;
					@Override
					public void run() {
						timeTaken++;

						if (rnd.nextBoolean()) {
							b.getWorld().spawnParticle(Particle.SMOKE_NORMAL,
									b.getLocation().clone().add(0.5, 0.5, 0.5), 1, 0.3, 0.3, 0.3, 0);
						}
						if (b.getType() != type){
							cancel();
						}
						if ((time - timeTaken) <= 1){
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

			private void markForChange(Block b, ItemStack is){
				int time = (rnd.nextInt(100) / 2 * 20) / 10;
				final Material type = b.getType();
				CancellableBukkitTask task = new CancellableBukkitTask() {
					private int timeTaken = 0;
					@Override
					public void run() {
						timeTaken++;
						if (rnd.nextBoolean()) {
							b.getWorld().spawnParticle(Particle.SMOKE_NORMAL,
									b.getLocation().clone().add(0.5, 0.5, 0.5), 1, 0.3, 0.3, 0.3, 0);
						}
						if (b.getType() != type){
							cancel();
						}
						if (rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean()){
							if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
								b.getWorld().spawn(b.getLocation().clone().add(0.5, 1, 0.5), EvokerFangs.class);
							}
						}
						if ((time - timeTaken) <= 1){
							b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
							b.setType(is.getType());
							b.setData(is.getData().getData());
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
					ItemProjectile pro = new ItemProjectile(item, e.getPlayer().getEyeLocation(), "orbofcorruption").
							shoot(e.getPlayer().getEyeLocation().getDirection().multiply(1).add(new Vector(0, 0.2, 0)));
					new ParticleTrail(pro.getEntity(), Particle.SMOKE_NORMAL, 0).startTrail();
				}
			}

			public void orbFunction(Location location){
				ArrayList<Block> blocks = BlockLooping.loopSphere(location.clone().add(0, 1, 0), 8, true);
				for (Block b : blocks){
					if (rnd.nextBoolean()) {
						if (destroy.contains(b.getType())) {
							markForDestroy(b);
						} else {
							if (natural.contains(b.getType())) {
								markForChange(b, changeTo[rnd.nextInt(changeTo.length - 1)]);
							}
						}
					}
				}
			}

			@EventHandler
			private void onItemLand(ItemProjectileLandEvent e){
				if (e.getProjectile().getData().equalsIgnoreCase("orbofcorruption")){
					e.removeEntity();
					orbFunction(e.getLocation());
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
