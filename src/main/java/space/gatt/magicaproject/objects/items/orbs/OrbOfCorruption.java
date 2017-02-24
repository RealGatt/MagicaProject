package space.gatt.magicaproject.objects.items.orbs;

import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class OrbOfCorruption implements Craftable{

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
