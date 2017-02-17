package space.gatt.magicaproject.managers;

public interface ManaStorable {
	public float getManaLevel();
	public void setManaLevel(float f);
	public float increaseMana(float f);
	public float decreaseMana(float f);
}
