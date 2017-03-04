package space.gatt.magicaproject.extra;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import space.gatt.magicaproject.MagicaMain;

public class ParticleTrail{

	private Entity ent;
	private Particle particle;
	private BukkitTask task;
	private float particleData;


	public ParticleTrail(Entity ent, Particle part, float particleData) {
		this.ent = ent;
		this.particle = part;
		this.particleData = particleData;
	}

	public Entity getEntity() {
		return ent;
	}

	public ParticleTrail startTrail(){
		task = Bukkit.getScheduler().runTaskTimer(MagicaMain.getMagicaMain(), ()->{
			getEntity().getWorld().spawnParticle(particle, getEntity().getLocation(), 1, 0, 0, 0, this.particleData);
			if (getEntity().isOnGround() || getEntity().isDead() || getEntity().getFireTicks() > 0){
				task.cancel();
			}
		}, 1, 1);
		return this;
	}

}