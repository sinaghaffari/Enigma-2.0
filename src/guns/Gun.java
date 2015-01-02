package guns;

import entities.Entity;
import org.newdawn.slick.Sound;

public class Gun {
    public double clipSize;
    public double currentClip;
    public double remainingTotalAmmo;
    public double damage;
    public double recoil;
    public double maxRecoil;
    public double recoilReduction;
    public double bulletOpacity;
    public byte smokeTrailAmount;
    protected Sound shotSound;
    protected String gunName;
    protected double rateOfFire;
    protected boolean isRapidFire;
    protected boolean isReloading;
    protected boolean isReloadingInterrupted;
    protected boolean isUnlocked;
    protected boolean canShootNow;
    protected long lastShot;

    public Gun() {
        clipSize = 0;
    }

    public String toString() {
        return gunName;

    }

    public void onUpdate( Entity entity ) {

    }

    public void playSound() {
        shotSound.play( 1, 1 );    //pitch, volume
    }
}
