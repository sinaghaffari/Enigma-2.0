package bullets;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Game;
import main.SVector2D;

import raycasting.RayCaster;
import raycasting.RayCollisionProfile;

import entities.Entity;
import guns.Gun;
import static org.lwjgl.opengl.GL11.*;

public class StandardBullet extends Bullet {


	public StandardBullet(	SVector2D initialPosition, double angle, Gun gunFiredFrom, Entity owner, double damage, int numberOfRicochet, Rectangle2D ignoredWall) {
		this.initialPosition = initialPosition;
		this.angle = angle;
		direction = SVector2D.createVectorGeometrically(angle, 1);
		this.parentGun = gunFiredFrom;
		this.damage = damage;
		this.owner = owner;
		this.prevWall = ignoredWall;
		finalPosition = calculateBullet(owner);
	}
	public void onUpdate() {
		double delta = Game.delta / 16.0;
		bulletStage += delta;
		if (bulletStage > (2 + delta)) {
			if (willRicochet && !ricochetDone) {
				ricochetDone = true;
				ricochet();
			}
		}
		if (bulletStage > (20 + delta)) {
			dead = true;
		}
	}
	public void onRender() {
		double delta = Game.delta / 16.0;
		glPushMatrix();
		{
			glLineWidth(2);
			if (bulletStage >= 0 && bulletStage < 2 + delta ) {
				glColor4d(1, 1, 0, 0.5 * parentGun.bulletOpacity);
				glBegin(GL_LINES);
				{
					glVertex2d(bulletLine.getX1(), bulletLine.getY1());
					glVertex2d(bulletLine.getX2(), bulletLine.getY2());
				}
				glEnd();
			} else {

				glBegin(GL_LINES);
				{
					glColor4d(1f, 1f, 1f, (1f)/(bulletStage * parentGun.smokeTrailAmount) * parentGun.bulletOpacity);
					glVertex2d(bulletLine.getX1(), bulletLine.getY1());
					glColor4d(1f, 1f, 1f, (2f)/(bulletStage * parentGun.smokeTrailAmount) * parentGun.bulletOpacity);
					glVertex2d(bulletLine.getX2(), bulletLine.getY2());
				}
				glEnd();
			}
		}
		glPopMatrix();

	}
	private void ricochet() {
		StandardBullet bullet = new StandardBullet(finalPosition, ricochetDirection.angle, parentGun, null, damage / 2.0, numberOfRicochet, prevWall);
		Game.bulletList.add(bullet);

	}
	public SVector2D calculateBullet(Entity owner) {
		RayCollisionProfile profile = RayCaster.castRay(initialPosition, angle, true, new Entity[] {owner}, new Rectangle2D[] {prevWall});
		if (profile != null) {
			if (profile.entityCollisions.size() != 0) {
				profile.entityCollisions.get(0).entity.onHitWithBullet(this, profile);
			} else {
				double deltaAngle = direction.opposite().angleBetween(profile.faceReferenceVector);
				double chance = (deltaAngle * 100)/Math.PI;
				if (Math.random() * (100 * (numberOfRicochet * 5) + 200) < chance) {
					numberOfRicochet++;
					willRicochet = true;
					if ((profile.faceReferenceVector.x > 0) || (profile.faceReferenceVector.x < 0)) {
						ricochetDirection = SVector2D.createVectorAlgebraically(-direction.x, direction.y);
					}
					if ((profile.faceReferenceVector.y > 0) ||(profile.faceReferenceVector.y < 0)) {
						ricochetDirection = SVector2D.createVectorAlgebraically(direction.x, -direction.y);
					}
					prevWall = profile.rec;
					ricochetDirection = SVector2D.createVectorGeometrically(ricochetDirection.angle + Math.toRadians(2 * (Math.random() - 0.5)), ricochetDirection.magnitude);
				}
			}
			this.bulletMovementVector = SVector2D.copyVector(profile.closestPosition.subtract(initialPosition));
			bulletLine.setLine(initialPosition.x, initialPosition.y, profile.closestPosition.x, profile.closestPosition.y);
			return profile.closestPosition;
		} else {
			this.dead = true;
			return null;
		}

	}
}


