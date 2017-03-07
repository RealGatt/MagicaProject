package space.gatt.magicaproject.interfaces;

public interface ManaStorable {
	float getManaLevel();

	void setManaLevel(float f);

	float increaseMana(float f);

	float decreaseMana(float f);

	float getMaxMana();

	boolean acceptsInput();
	boolean allowsOutput();
}
