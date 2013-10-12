package guns;

import java.awt.geom.Rectangle2D;

import main.Game;
import main.SVector2D;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import bullets.StandardBullet;

import entities.Entity;

public class GunMP5 extends Gun {
	public final double clipSize = 30;
	public GunMP5() {
		super.gunName = "H&K MP5";
		super.damage = 10;
		super.rateOfFire = 67.5675675676;
		super.isRapidFire = true;
		super.recoil = 1;
		super.maxRecoil = 7;
		super.recoilReduction = 0.1;
		super.isReloading = false;
		super.isReloadingInterrupted = false;
		super.isUnlocked = true;
		super.canShootNow = true;
		super.lastShot = System.nanoTime();
		super.bulletOpacity = 1;
		super.smokeTrailAmount = 4;
		
		try {
			shotSound= new Sound("data/sounds/"+gunName+".wav");
		} catch (SlickException e) {}
	}

	public void onUpdate(Entity entity) {
		if (entity.guns.get(entity.currentGun) == this) {
			if ((System.nanoTime() - lastShot) / 1000000.0 >= rateOfFire) {
				if (entity.isTriggerDown) {
					lastShot = System.nanoTime();
					shoot(entity);
				}
			}
		}
	}
	public void shoot(Entity entity) {
		Game.bulletList.add(new StandardBullet(entity.position.add(SVector2D.createVectorGeometrically(entity.facingAngle, 15)), entity.facingAngle, this, entity, this.damage, 0, new Rectangle2D.Double()));
		entity.gunRecoilBeingFelt += this.recoil;
		entity.entityShake = true;
		if (entity == Game.playerObj)
			Game.setScreenShootShake((Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0), (Math.random() - 0.5) * 2 * ((entity.totalRecoilBeingFelt + this.recoil) / 2.0));

	
		playSound();
	}
	public void reload() {

	}

}
