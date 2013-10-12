package entities;

import static org.lwjgl.opengl.GL11.*;

import guns.GunM16A4;
import guns.GunMP5;
import guns.GunP99;
import guns.GunUSAS_12;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import main.Consol;
import main.Game;
import main.SVector2D;
import main.Util;
import map.Chunk;
import map.ChunkManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.ShapeRenderer;

import raycasting.RayCaster;
import raycasting.RayCollisionProfile;


public class EntityPlayer extends Entity {
	double maxSpeed = 3;
	public double currentTargetX;
	public double currentTargetY;
	public Line2D playerRay = new Line2D.Double();
	public Line2D recoilLine1 = new Line2D.Double();
	public Line2D recoilLine2 = new Line2D.Double();
	public Line2D playerFacingRay = new Line2D.Double();
	double virtualFacingAngle = 0;
	ArrayList<Rectangle2D> rectangles = new ArrayList<Rectangle2D>();

	public EntityPlayer( SVector2D position, double facingAngle ) {
		this.entityType = EntityType.PLAYER;
		guns.add(new GunP99());
		guns.add(new GunMP5());
		guns.add(new GunM16A4(99999));
		guns.add(new GunUSAS_12());
		super.position = position;
		this.facingAngle = facingAngle;
		currentTargetX = Game.mapMouseX;
		currentTargetY = Game.mapMouseY;
		ambientRecoilBeingFelt = 2;
	}
	public void onUpdate() {
		if (Game.keyPressed.contains(Keyboard.KEY_SPACE)) {
			forcePush( 10, 100 );
		}
		displacePlayer();
		Game.setTLCs();
		orientPlayer();
		castPlayerRay();
		maintainGuns();
		maintainRecoil();
		boundingBox.setRect(position.x - 15, position.y - 15, 30, 30);
		super.onUpdate();
	}
	public void forcePush( int strength, int radius ) {
		for (Entity e : EntityManager.globalEntityList) {
			if (e != this) {
				double distance = Util.distance(position.x, position.y, e.position.x, e.position.y);
				if (distance < radius) {
					double angle = Util.angleOfLineRad(position.x, position.y, e.position.x, e.position.y);
					double magnitude = ((1/(distance/radius)) - 1) * strength;
					SVector2D force = SVector2D.createVectorGeometrically(angle, magnitude);
					e.addForce(force);
				}
			}
		}
		
	}
	public void castPlayerRay() {
		SVector2D source = position.add(SVector2D.createVectorGeometrically(facingAngle, 15));
		RayCollisionProfile facingRay = RayCaster.castRay(source, facingAngle, false, new Entity[] {this}, new Rectangle2D[] {});
		RayCollisionProfile lookingRay = RayCaster.castRay(source, virtualFacingAngle, false, new Entity[] {this}, new Rectangle2D[] {});
		RayCollisionProfile recoilRay1 = RayCaster.castRay(source, virtualFacingAngle + (Math.toRadians(totalRecoilBeingFelt)), false, new Entity[] {this}, new Rectangle2D[] {});
		RayCollisionProfile recoilRay2 = RayCaster.castRay(source, virtualFacingAngle - (Math.toRadians(totalRecoilBeingFelt)), false, new Entity[] {this}, new Rectangle2D[] {});
		if (facingRay != null) {
			playerRay.setLine(source.x, source.y, facingRay.closestPosition.x, facingRay.closestPosition.y);
		}
		if (lookingRay != null) {
			playerFacingRay.setLine(source.x, source.y, lookingRay.closestPosition.x, lookingRay.closestPosition.y);
		}
		if (recoilRay1 != null) {
			recoilLine1.setLine(source.x, source.y, recoilRay1.closestPosition.x, recoilRay1.closestPosition.y);
		}
		if (recoilRay2 != null) {
			recoilLine2.setLine(source.x, source.y, recoilRay2.closestPosition.x, recoilRay2.closestPosition.y);
		}
	}
	public void onRender() {
		double x = position.x;
		double y = position.y;
		glPushMatrix();
		{
			glTranslated(x, y, 0);
			{
				glRotated(Math.toDegrees(facingAngle), 0, 0, 1);
				{
					glColor3f(0, 0, 1);
					ShapeRenderer.draw(new Circle(0, 0, (float) this.radius));
					ShapeRenderer.draw(new Line(0, 0, (float) radius, 0));
				}
			}
		}
		glPopMatrix();
		glPushMatrix();
		{
			glBegin(GL_LINES);
			if (Consol.viewRays[0]) {
				glColor4f(1, 0, 0, 0.3f);
				{
					glVertex2d( playerRay.getX1(), playerRay.getY1() );
					glVertex2d(  playerRay.getX2(), playerRay.getY2() );
				}
			}
			if (Consol.viewRays[1]) {
				glColor4f(0, 0, 1, 0.3f);
				glBegin(GL_LINES);
				{
					glVertex2d( playerFacingRay.getX1(), playerFacingRay.getY1() );
					glVertex2d(  playerFacingRay.getX2(), playerFacingRay.getY2() );
				}
			}
			if (Consol.viewRays[2]) {
				glColor4f(0, 1, 0, 0.3f);
				{
					glVertex2d( recoilLine1.getX1(), recoilLine1.getY1() );
					glVertex2d(  recoilLine1.getX2(), recoilLine1.getY2() );
				}
				glBegin(GL_LINES);
				{
					glVertex2d( recoilLine2.getX1(), recoilLine2.getY1() );
					glVertex2d(  recoilLine2.getX2(), recoilLine2.getY2() );
				}
			}
			glEnd();
		}
		glPopMatrix();
		if (Consol.wallCollisions) {
			for (int a = 0; a < rectangles.size(); a++) {
				glColor3f(1, 0, 1);
				glBegin(GL_QUADS);
				{
					glVertex2d(rectangles.get(a).getMinX(), rectangles.get(a).getMinY());
					glVertex2d(rectangles.get(a).getMaxX(), rectangles.get(a).getMinY());
					glVertex2d(rectangles.get(a).getMaxX(), rectangles.get(a).getMaxY());
					glVertex2d(rectangles.get(a).getMinX(), rectangles.get(a).getMaxY());
				}
				glEnd();
			}
		}
	}
	private void maintainGuns() {
		isTriggerDown = Mouse.isButtonDown(0);
		if (Game.keyPressed.contains(Keyboard.KEY_E)) {
			currentGun++;

			if (currentGun == guns.size()) {
				currentGun = 0;
			}
			System.out.println("Clip Size: " + guns.get(currentGun).clipSize);
			System.out.println("Current Clip: " + guns.get(currentGun).currentClip);
			System.out.println("Total Ammo: " + guns.get(currentGun).remainingTotalAmmo);
		} else if (Game.keyPressed.contains(Keyboard.KEY_Q)) {
			currentGun--;
			if (currentGun == -1) {
				currentGun = (byte) (guns.size() - 1);
			}
			System.out.println("Clip Size: " + guns.get(currentGun).clipSize);
			System.out.println("Current Clip: " + guns.get(currentGun).currentClip);
			System.out.println("Total Ammo: " + guns.get(currentGun).remainingTotalAmmo);
		}
		for (int a = 0; a < guns.size(); a++) {
			guns.get(a).onUpdate(this);
		}

	}
	private void maintainRecoil() {
		double delta = Game.delta / 16.0;
		if (gunRecoilBeingFelt > 0) {
			gunRecoilBeingFelt -= guns.get(currentGun).recoilReduction * delta;
			if (gunRecoilBeingFelt > guns.get(currentGun).maxRecoil) {
				gunRecoilBeingFelt = guns.get(currentGun).maxRecoil;
			}
		}
		if (gunRecoilBeingFelt <= 0){
			gunRecoilBeingFelt = 0;
		}
		totalRecoilBeingFelt = gunRecoilBeingFelt + turningRecoilBeingFelt + ambientRecoilBeingFelt + movementRecoilBeingFelt;
	}
	private void displacePlayer() {
		double delta = Game.delta / 16.0;
		SVector2D acceleration = SVector2D.createZeroVector();
		SVector2D voluntaryAcceleration = calculateVoluntaryAcceleration();
		acceleration = acceleration.add(voluntaryAcceleration);
		acceleration = acceleration.add(poolForces());
		velocity = velocity.add(acceleration);
		acceleration = SVector2D.createZeroVector();
		acceleration = velocity.opposite().multiply(0.3);
		if (velocity.magnitude - acceleration.magnitude >= 0) {
			velocity = velocity.add(acceleration);
		} else {
			velocity = SVector2D.createZeroVector();
		}
		collideWithWorld();
		position = position.add(velocity.multiply(delta));
		movementRecoilBeingFelt = velocity.magnitude;
	}
	private SVector2D calculateVoluntaryAcceleration() {
		SVector2D walkingDirection = SVector2D.createZeroVector();
		double speed = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speed = 2;
			} else {
				speed = 1;
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			walkingDirection = walkingDirection.add(SVector2D.createVectorGeometrically(Math.toRadians(270), 1));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			walkingDirection = walkingDirection.add(SVector2D.createVectorGeometrically(Math.toRadians(90), 1));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			walkingDirection = walkingDirection.add(SVector2D.createVectorGeometrically(Math.toRadians(180), 1));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			walkingDirection = walkingDirection.add(SVector2D.createVectorGeometrically(Math.toRadians(0), 1));
		}
		return SVector2D.createVectorGeometrically(walkingDirection.angle, speed);
	}
	private void orientPlayer() {
		double delta = Game.delta / 16.0;
		double virtualMouseActualMouseDistance = Util.distance(currentTargetX, currentTargetY, Game.displayMouseX, Game.displayMouseY);
		double virtualMouseActualMouseAngle    = Util.angleOfLineRad(currentTargetX, currentTargetY, Game.displayMouseX, Game.displayMouseY);
		turningRecoilBeingFelt = virtualMouseActualMouseDistance / 10.0;
		virtualFacingAngle = 0;
		currentTargetX += (((virtualMouseActualMouseDistance / (2.0)) * delta) * Math.cos(virtualMouseActualMouseAngle));
		currentTargetY += (((virtualMouseActualMouseDistance / (2.0)) * delta) * Math.sin(virtualMouseActualMouseAngle));
		virtualFacingAngle = Util.angleOfLineRad(position.x - Game.tlcx, position.y - Game.tlcy, currentTargetX, currentTargetY);
		if (entityShake) {
			entityShake = false;
			entityShakeRecoil += (Math.random() - 0.5) * totalRecoilBeingFelt;
		}
		facingAngle = virtualFacingAngle + Math.toRadians(entityShakeRecoil);
		entityShakeRecoil /= 1 + ((guns.get(currentGun).recoilReduction / 2.0) * delta * delta);
	}
}
