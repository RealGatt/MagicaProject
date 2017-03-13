package space.gatt.magicaproject.managers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.objects.items.wand.Wand;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;
import java.util.Collections;

public class RecipeManager implements Listener{

	private ArrayList<MagicaRecipe> recipes = new ArrayList<>();


	public RecipeManager() {
		Bukkit.getPluginManager().registerEvents(this, MagicaMain.getMagicaMain());
	}

	public void registerRecipe(MagicaRecipe rec){
		recipes.add(rec);
	}

	@EventHandler
	public void onItemAdd(EventAddItemToRecipe e) {
		Collections.shuffle(this.recipes);
		e.getCrafter().setRecipeCanCraft(null);
		e.getCrafter().setRecipeCraftTime(-1);
		e.getCrafter().getBlockDisplayName().setDoesDisplayName(false);
		for (MagicaRecipe recipe : this.recipes){
			if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), recipe.getRequirements())) {
				if (recipe.doesRequireWand()){
					if (e.getCrafter().hasWand()) {

						float time = ((recipe.getTimeInTicks()
								/ Wand.getRecipeForWand(e.getCrafter().getWand().getItemStack()).getTimeInTicks()
						) * 1000);
						e.getCrafter().setRecipeCanCraft(recipe);
						e.getCrafter().setRecipeCraftTime(time);
						e.getCrafter().getBlockDisplayName().setDisplay(
								"&aReady to Craft! &7- &e" +
								(recipe.getCraftedItem().getItemMeta().hasDisplayName() ?
										recipe.getCraftedItem().getItemMeta().getDisplayName() : recipe.getCraftedItem().getType().name().replaceAll("_", " ")));
						e.getCrafter().getBlockDisplayName().setDoesDisplayName(true);
						return;
					}
					return;
				}
				e.getCrafter().setRecipeCanCraft(recipe);
				e.getCrafter().setRecipeCraftTime(recipe.getTimeInTicks());
				e.getCrafter().getBlockDisplayName().setDisplay(
						"&aReady to Craft! &7- &e" +
						(recipe.getCraftedItem().getItemMeta().hasDisplayName() ?
								recipe.getCraftedItem().getItemMeta().getDisplayName() : recipe.getCraftedItem().getType().name().replaceAll("_", " ")));
				e.getCrafter().getBlockDisplayName().setDoesDisplayName(true);
			}
		}
	}
}
