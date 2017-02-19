package space.gatt.magicaproject.managers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.gatt.magicaproject.MagicaMain;
import space.gatt.magicaproject.events.EventAddItemToRecipe;
import space.gatt.magicaproject.extra.MagicaRecipe;
import space.gatt.magicaproject.utilities.BaseUtils;

import java.util.ArrayList;

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
		Bukkit.broadcastMessage("Item Added");
		for (MagicaRecipe recipe : this.recipes){
			if (BaseUtils.isSameListItems(e.getCrafter().getItemsAsStack(), recipe.getRequirements())) {
				Bukkit.broadcastMessage("Can Craft Item");
				e.getCrafter().beginCrafting(recipe.getCraftedItem(), recipe.getTimeInTicks(), recipe.getManaPerTick(), e.getPlayer());
			}
		}
	}

}
