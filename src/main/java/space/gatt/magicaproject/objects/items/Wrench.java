package space.gatt.magicaproject.objects.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Directional;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class Wrench extends Craftable {

	public static void registerItemListener(){
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onWrenchUse(PlayerInteractEvent e){
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null && e.getItem() == getStaticCraftedItem()){
					e.setUseInteractedBlock(Event.Result.DENY);
					e.setUseItemInHand(Event.Result.DENY);
					if (e.getClickedBlock() instanceof Directional){
						Directional d = (Directional)e.getClickedBlock();
						boolean next = false;
						for (BlockFace bf : BlockFace.values()){
							if (next){
								d.setFacingDirection(bf);
								return;
							}
							if (bf == d.getFacing()){
								next = true;
							}
						}
						d.setFacingDirection(BlockFace.values()[0]);
						e.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE_NORMAL, e.getClickedBlock().getLocation(),
								15, 0.3, 0.3, 0.3);
						e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
					}
				}
			}
		}, MagicaMain.getMagicaMain());
	}

	public static ItemStack getStaticCraftedItem() {
		ItemStack manaGenerator = new ItemStack(Material.STONE_SPADE);
		ItemMeta im = manaGenerator.getItemMeta();
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&7Wrench"));
		im.addItemFlags(ItemFlag.values());
		im.setUnbreakable(true);
		im.setLore(MagicaMain.getLoreLine());
		manaGenerator.setItemMeta(im);
		return manaGenerator;
	}

	@Override
	public String getItemName() {
		return "Wrench";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 = new MagicaRecipe(new ArrayList<>(Arrays.asList(new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.IRON_INGOT),
				new ItemStack(Material.STICK),
				new ItemStack(Material.IRON_NUGGET))), getStaticCraftedItem(), 40, 0);
		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
