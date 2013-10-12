package guns;

import org.newdawn.slick.Sound;

import entities.Entity;

public class Gun {
	protected Sound shotSound;
	protected String gunName;
	public double clipSize;
	public double currentClip;
	public double remainingTotalAmmo;
	public double damage;
	protected double rateOfFire;
	protected boolean isRapidFire;
	public double recoil;
	public double maxRecoil;
	public double recoilReduction;
	protected boolean isReloading;
	protected boolean isReloadingInterrupted;
	protected boolean isUnlocked;
	protected boolean canShootNow;
	protected long lastShot;
	public double bulletOpacity;
	public byte smokeTrailAmount;
	public Gun() {
		clipSize = 0;
	}
	public String toString() {
		return gunName;

	}
	public void onUpdate(Entity entity) {

	}

	public void playSound(){
		shotSound.play(1, 1);	//pitch, volume
	}
}
