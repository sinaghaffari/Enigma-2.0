package guns;

import bullets.StandardBullet;
import entities.Entity;
import main.Game;
import main.SVector2D;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.awt.geom.Rectangle2D;

public class GunP99 extends Gun {
    public final double clipSize = 12;

    public GunP99() {
        super.gunName = "Walther P99";
        super.damage = 10;
        super.rateOfFire = 50;
        super.isRapidFire = false;
        super.recoil = 2;
        super.maxRecoil = 5;
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

    public void onUpdate( Entity entity ) {
        if ( entity.guns.get( entity.currentGun ) == this ) {
            if ( (System.nanoTime() - lastShot) / 1000000.0 >= rateOfFire ) {
                if ( entity.isTriggerDown ) {
                    if ( canShootNow ) {
                        lastShot = System.nanoTime();
                        canShootNow = false;
                        shoot( entity );
                    }
                } else {
                    canShootNow = true;
                }
            }
        }
    }

    public void shoot( Entity entity ) {
        Game.bulletList.add( new StandardBullet( entity.position.add( SVector2D.createVectorGeometrically( entity.facingAngle, 15 ) ), entity.facingAngle, this, entity, this.damage, 0, new Rectangle2D.Double() ) );
        entity.gunRecoilBeingFelt += this.recoil;
        entity.entityShake = true;
        if ( entity == Game.playerObj )
            Game.setScreenShootShake( (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0), (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0) );

        this.playSound();

    }

    public void reload() {

    }

}
