package guns;

import bullets.StandardBullet;
import entities.Entity;
import main.Game;
import main.SVector2D;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.awt.geom.Rectangle2D;


public class GunM16A4 extends Gun {

    double delayBetweenShots = 67;
    long lastIndividualShot = System.nanoTime();
    boolean shooting = false;
    int numberOfBulletsPerBurst = 3;
    int bulletsFiredInBurstSoFar = 0;

    public GunM16A4( double rounds ) {
        double tempR = rounds;
        super.clipSize = 30;
        if ( tempR > clipSize ) {
            super.currentClip = clipSize;
            tempR -= clipSize;
            super.remainingTotalAmmo = tempR;
        } else {
            super.currentClip = clipSize;
        }
        super.gunName = "Colt M16A4";
        super.damage = 10;
        super.rateOfFire = 500;
        super.isRapidFire = true;
        super.recoil = 1.5;
        super.maxRecoil = 9;
        super.recoilReduction = 0.1;
        super.isReloading = false;
        super.isReloadingInterrupted = false;
        super.isUnlocked = true;
        super.canShootNow = true;
        super.lastShot = System.nanoTime();
        super.bulletOpacity = 1;
        super.smokeTrailAmount = 2;

        try {
            shotSound = new Sound( "data/sounds/" + gunName + ".wav" );
        } catch ( SlickException e ) {
        }
    }

    public GunM16A4() {
        this( 30 );
    }

    public void onUpdate( Entity entity ) {
        if ( entity.guns.get( entity.currentGun ) == this ) {
            if ( (System.nanoTime() - lastShot) / 1000000.0 >= rateOfFire ) {
                if ( entity.isTriggerDown ) {
                    if ( canShootNow || isRapidFire ) {
                        canShootNow = false;
                        lastShot = System.nanoTime();
                        shooting = true;
                    }
                } else {
                    canShootNow = true;
                }
            }
        }
        if ( shooting ) {
            if ( (System.nanoTime() - lastIndividualShot) / 1000000.0 >= delayBetweenShots ) {
                lastIndividualShot = System.nanoTime();
                bulletsFiredInBurstSoFar++;
                shoot( entity );
                if ( bulletsFiredInBurstSoFar == numberOfBulletsPerBurst ) {
                    bulletsFiredInBurstSoFar = 0;
                    shooting = false;
                }
            }
        }
    }

    public void shoot( Entity entity ) {
        Game.bulletList.add( new StandardBullet( entity.position.add( SVector2D.createVectorGeometrically( entity.facingAngle, 15 ) ), entity.facingAngle, this, entity, this.damage, 0, new Rectangle2D.Double() ) );
        if ( bulletsFiredInBurstSoFar == numberOfBulletsPerBurst ) {
            entity.gunRecoilBeingFelt += this.recoil * 4;
        } else {
            entity.gunRecoilBeingFelt += this.recoil;
        }
        entity.entityShake = true;
        if ( entity == Game.playerObj )
            Game.setScreenShootShake( (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0), (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0) );

        playSound();
    }

    public void reload() {

    }

}
