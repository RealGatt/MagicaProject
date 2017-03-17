package space.gatt.magicaproject.objects.items.armor;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.interfaces.Craftable;
import space.gatt.magicaproject.objects.items.MagicaShard;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class WingedBoots implements Craftable {

	public static ItemStack getStaticCraftedItem() {
		ItemStack shoes = MagicaMain.getCustomItem(Material.LEATHER_BOOTS, (short)1);
		LeatherArmorMeta im = (LeatherArmorMeta)shoes.getItemMeta();
		im.setColor(Color.WHITE);
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.setDisplayName(BaseUtils.colorString("&eWinged Boots"));
		im.addItemFlags(ItemFlag.values());
		im.setLore(Arrays.asList(
				BaseUtils.colorString("&eMana Stored: &b0&7/&e1000000"),
				BaseUtils.colorString("&eAllows for flight!"),
				BaseUtils.colorString("&eRecharge in a &9Magica Redirector"),
				"",
				MagicaMain.getLoreLine().get(0)));
		im.setUnbreakable(true);
		shoes.setItemMeta(im);
		return shoes;
	}

	@Override
	public String getItemName() {
		return "Winged Boots";
	}

	public static ArrayList<MagicaRecipe> getStaticRecipes(){
		ArrayList<MagicaRecipe> recipes = new ArrayList<>();
		MagicaRecipe rec1 =
				new MagicaRecipe(new ArrayList<>(Arrays.asList(
						new ItemStack(Material.FEATHER),
						new ItemStack(Material.FEATHER),
						new ItemStack(Material.FEATHER),
						new ItemStack(Material.FEATHER),
						new ItemStack(Material.LEATHER_BOOTS),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem(),
						MagicaShard.getStaticCraftedItem())), getStaticCraftedItem(), 2000);

		recipes.add(rec1);
		return recipes;
	}

	@Override
	public ArrayList<MagicaRecipe> getRecipes() {
		return getStaticRecipes();
	}
}
