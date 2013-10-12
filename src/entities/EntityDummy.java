package entities;

import static org.lwjgl.opengl.GL11.*;

import main.Game;
import main.SVector2D;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.ShapeRenderer;

public class EntityDummy extends Entity {
	int step;
	public EntityDummy( SVector2D position, double facingAngle ) {
		this.entityType = EntityType.PLAYER;
		super.position = position;
		this.facingAngle = facingAngle;
		ambientRecoilBeingFelt = 2;
		this.health = 10;
		this.entityType=EntityType.DUMMY;
	}
	public void onUpdate() {
		displacePlayer();
		boundingBox.setRect(position.x - 15, position.y - 15, 30, 30);
		super.onUpdate();
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
					glColor3f(0, 0, 0);
					if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
						glBegin(GL_LINE_LOOP);
						{
							glVertex2d(-radius, -radius);
							glVertex2d(radius, -radius);
							glVertex2d(radius, radius);
							glVertex2d(-radius, radius);
						}
						glEnd();
					} else {
						ShapeRenderer.draw(new Circle(0, 0, (float) this.radius));
						ShapeRenderer.draw(new Line(0, 0, (float) radius, 0));
					}
				}
			}
		}
		glPopMatrix();
		/*glPushMatrix();
		{
			path = Map.meshMap.findPath((float)this.position.x, (float)this.position.y, (float)Game.playerObj.position.x, (float)Game.playerObj.position.y, false);
			glColor3f(1, 0, 0);
			double lastX = path.getX(0);
			double lastY = path.getY(0);
			for (int a = 1; a < path.length(); a++) {
				ShapeRenderer.draw(new Line((float)lastX, (float)lastY, path.getX(a), path.getY(a)));
				lastX = path.getX(a);
				lastY = path.getY(a);
			}

		}
		glPopMatrix();*/
	}
	private void displacePlayer() {
		double delta = Game.delta / 16.0;
		SVector2D acceleration = SVector2D.createZeroVector();
		acceleration = acceleration.add(poolForces());
		velocity = velocity.add(acceleration);
		acceleration = SVector2D.createZeroVector();
		acceleration = velocity.opposite().multiply(0.3);
		if (velocity.magnitude - acceleration.magnitude >= 0) {
			//velocity = velocity.add(acceleration);
		} else {
			velocity = SVector2D.createZeroVector();
		}
		collideWithWorld();
		position = position.add(velocity.multiply(delta));
		movementRecoilBeingFelt = velocity.magnitude;
	}
}
