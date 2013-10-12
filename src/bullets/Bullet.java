package bullets;

import entities.Entity;
import guns.Gun;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import main.SVector2D;

public class Bullet {
	public SVector2D initialPosition;
	public SVector2D finalPosition;
	public SVector2D bulletMovementVector;
	public SVector2D direction;
	public Line2D bulletLine = new Line2D.Double();
	public Gun parentGun;	
	public double angle;
	public double damage;
	public double bulletStage = 0;
	public boolean dead = false;
	public double opacity;
	public Entity owner;
	public int numberOfRicochet;
	public double normalAngle;
	public double deltaAngle;
	public boolean ricochetDone = false;
	public boolean willRicochet = false;
	public SVector2D ricochetDirection = SVector2D.createZeroVector();
	public Rectangle2D prevWall = new Rectangle2D.Double();
	
	public void onUpdate() {

	}
	public void onRender() {
		
	}

}
