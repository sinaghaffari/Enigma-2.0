package entities;


import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2d;

import guns.GunMP5;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Consol;
import main.Game;
import main.SVector2D;
import main.Util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.ShapeRenderer;

import raycasting.RayCaster;
import raycasting.RayCollisionProfile;


public class EntityTurret extends Entity {

	Entity target = null;
	ArrayList<Rectangle2D> rectangles = new ArrayList<Rectangle2D>();
	RayCollisionProfile aimingRay;
	RayCollisionProfile facingRay;
	RayCollisionProfile lookingRay;
	RayCollisionProfile recoilRay1;
	RayCollisionProfile recoilRay2;
	double angleToTarget;
	public Line2D ray = new Line2D.Double();
	int aimingX,aimingY;
	static final int RANGE = 600;
	int fireDelay = 0;
	double distanceFromTarget;
	double virtualFacingAngle;


	public EntityTurret(SVector2D pos, double facingAngle){
		this.entityType = EntityType.TURRET;
		this.facingAngle = facingAngle;
		health = 20;
		position = pos;
		guns.add(new GunMP5());
		this.ambientRecoilBeingFelt = 2;
		this.isFixed = true;
	}
	public void onUpdate(){
		double delta = Game.delta / 16.0;
		if (target!=null) {
			distanceFromTarget = Util.distance(position.x, position.y, target.position.x, target.position.y);
			angleToTarget = Util.angleOfLineRad(position.x, position.y, target.position.x, target.position.y);
			aimingRay = RayCaster.castRay(position, facingAngle, false, new Entity[] {this}, new Rectangle2D[] {});
			if (distanceFromTarget<RANGE) {
				rotate();
				if (aimingRay.entityCollisions.size() != 0) {
					if(aimingRay.entityCollisions.get(0).entity.entityType == target.entityType) {
						isTriggerDown = true;	
					} else {
						target = null;
					}
				}
			} else {
				target = null;
			}
		}

		if (target == null || target.dead) {
			isTriggerDown = false;
			target = findTarget();
		}
		castRay();
		maintainGuns();
		maintainRecoil();
		orient();
		//SVector2D acceleration = poolForces();
		//velocity = velocity.add(acceleration);
		//position = position.add(velocity.multiply(delta));
		collideWithWorld();
		boundingBox.setRect(position.x - 15, position.y - 15, 30, 30);
		super.onUpdate();
	}
	private void maintainGuns() {
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
		totalRecoilBeingFelt = gunRecoilBeingFelt + ambientRecoilBeingFelt + turningRecoilBeingFelt;
	}
	private void orient() {
		double delta = Game.delta / 16.0;
		if (entityShake) {
			entityShake = false;
			entityShakeRecoil += (Math.random() - 0.5) * 4 * guns.get(currentGun).recoil;
		}
		facingAngle = virtualFacingAngle + Math.toRadians(entityShakeRecoil);
		entityShakeRecoil /= 1 + ((guns.get(currentGun).recoilReduction / 2.0) * delta * delta);
	}
	public Entity findTarget() {
		double closestDistance = Double.POSITIVE_INFINITY;
		Entity closestEnemy = null;
		for (Entity e: EntityManager.globalEntityList){
			if (e.entityType == EntityType.DUMMY || e.entityType == EntityType.ENEMY){
				double distance = Util.distance(position.x, position.y, e.position.x, e.position.y);
				double angle = Util.angleOfLineRad(position.x, position.y, e.position.x, e.position.y);
				RayCollisionProfile ray = RayCaster.castRay(position, angle, false, new Entity[] {this}, new Rectangle2D[] {});
				if (distance<closestDistance && distance<RANGE && ray.entityCollisions.size() != 0){
					if (ray.entityCollisions.get(0).entity.entityType == e.entityType) {
						closestDistance = Util.distance(position.x, position.y, e.position.x, e.position.y);
						closestEnemy = e;
					}
				}
			}
		}
		return closestEnemy;
	}

	public void rotate(){
		double delta = Game.delta / 16.0;
		double angle = Util.angleOfLineRad(position.x, position.y, target.position.x, target.position.y);
		double deltaAngle = SVector2D.createVectorGeometrically(angle, 1).angleBetween(SVector2D.createVectorGeometrically(facingAngle, 1)) / 10 * delta;
		double crossDirection = SVector2D.createVectorGeometrically(angle, 1).cross(SVector2D.createVectorGeometrically(facingAngle, 1));
		virtualFacingAngle += -deltaAngle * crossDirection;
		turningRecoilBeingFelt = deltaAngle;

	}

	public void castRay(){
		facingRay = RayCaster.castRay(position, facingAngle, false, new Entity[] {this}, new Rectangle2D[] {});
		lookingRay = RayCaster.castRay(position.add(SVector2D.createVectorGeometrically(virtualFacingAngle, 15)), virtualFacingAngle, false, new Entity[] {this}, new Rectangle2D[] {});
		recoilRay1 = RayCaster.castRay(position.add(SVector2D.createVectorGeometrically(virtualFacingAngle, 15)), virtualFacingAngle + (Math.toRadians(totalRecoilBeingFelt)), false, new Entity[] {this}, new Rectangle2D[] {});
		recoilRay2 = RayCaster.castRay(position.add(SVector2D.createVectorGeometrically(virtualFacingAngle, 15)), virtualFacingAngle - (Math.toRadians(totalRecoilBeingFelt)), false, new Entity[] {this}, new Rectangle2D[] {});
	}

	public void onRender(){
		double x = position.x;
		double y = position.y;
		glPushMatrix();
		{

			glTranslated(x, y, 0);
			{
				glRotated(Math.toDegrees(facingAngle), 0, 0, 1);
				{
					glColor3f(0.5f, 1, 0);
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
					glVertex2d( position.x, position.y );
					glVertex2d(  facingRay.closestPosition.x, facingRay.closestPosition.y );
				}
			}
			if (Consol.viewRays[1]) {
				glColor4f(0, 0, 1, 0.3f);
				glBegin(GL_LINES);
				{
					glVertex2d( position.x, position.y );
					glVertex2d(  lookingRay.closestPosition.x, lookingRay.closestPosition.y );
				}
			}
			if (Consol.viewRays[2]) {
				glColor4f(0, 1, 0, 0.3f);
				{
					glVertex2d( position.x, position.y );
					glVertex2d(  recoilRay1.closestPosition.x, recoilRay1.closestPosition.y );
				}
				glBegin(GL_LINES);
				{
					glVertex2d( position.x, position.y );
					glVertex2d(  recoilRay2.closestPosition.x, recoilRay2.closestPosition.y );
				}
			}
			glEnd();
		}
		glPopMatrix();
	}
}
