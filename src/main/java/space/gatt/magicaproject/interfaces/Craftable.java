package space.gatt.magicaproject.interfaces;

import space.gatt.magicaproject.extra.MagicaRecipe;

import java.util.ArrayList;

public interface Craftable {

	public String getItemName();

	public ArrayList<MagicaRecipe> getRecipes();
}
