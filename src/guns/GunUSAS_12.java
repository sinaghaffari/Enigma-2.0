package guns;

import bullets.StandardBullet;
import entities.Entity;
import main.Game;
import main.SVector2D;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.awt.geom.Rectangle2D;

public class GunUSAS_12 extends Gun {
    public final double clipSize = 6;

    public GunUSAS_12() {
        gunName = "DPI USAS-12";
        damage = 10;
        rateOfFire = 230.76;
        isRapidFire = true;
        recoil = 7;
        maxRecoil = 10;
        recoilReduction = 0.1;
        isReloading = false;
        isReloadingInterrupted = false;
        isUnlocked = true;
        canShootNow = true;
        lastShot = System.nanoTime();
        bulletOpacity = 1 / 2.0;
        smokeTrailAmount = 4;

        try {
            shotSound = new Sound( "data/sounds/" + gunName + ".wav" );
        } catch ( SlickException e ) {
        }
    }

    public void onUpdate( Entity entity ) {
        if ( entity.guns.get( entity.currentGun ) == this ) {
            if ( (System.nanoTime() - lastShot) / 1000000.0 >= rateOfFire ) {
                if ( entity.isTriggerDown ) {
                    lastShot = System.nanoTime();
                    shoot( entity );
                }
            }
        }
    }

    public void shoot( Entity entity ) {
        for ( int a = 0; a < 9; a++ ) {
            double angle = (Math.random() - 0.5) * 10;
            Game.bulletList.add( new StandardBullet( entity.position.add( SVector2D.createVectorGeometrically( entity.facingAngle, 15 ) ), entity.facingAngle + Math.toRadians( angle ), this, entity, this.damage, 0, new Rectangle2D.Double() ) );
        }
        entity.gunRecoilBeingFelt += this.recoil;
        entity.entityShake = true;
        if ( entity == Game.playerObj )
            Game.setScreenShootShake( (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0), (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0) );

        playSound();

    }

    public void reload() {

    }

}
