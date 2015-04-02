package entities;


import main.Block;
import main.Game;
import main.SVector2D;
import main.Util;
import map.Map;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.util.pathfinding.Path;
import pathfinding.MapPathfinding;

import java.awt.geom.Rectangle2D;

import static org.lwjgl.opengl.GL11.*;

public class EntityEnemy extends Entity {
    protected double speed;
    protected double damage;
    protected int size;
    private double nextX, nextY, lastX, lastY;
    private Path path = new Path();

    public EntityEnemy( double pointX, double pointY ) {
        this( (int) pointX / 20, (int) pointY / 20 );
    }

    public EntityEnemy( int spawnX, int spawnY ) {
        this.boundingBox = new Rectangle2D.Double();
        this.entityType = EntityType.ENEMY;
        position = SVector2D.createVectorAlgebraically( spawnY * Map.BLOCK_SIZE + Map.BLOCK_SIZE / 2, spawnX * Map.BLOCK_SIZE + Map.BLOCK_SIZE / 2 );
        speed = 1;
        size = 30;
        health = 10;
    }

    public void onUpdate() { //for later reference, checking line through all rectangles takes ~0.25ms
        updatePath();
        chooseBlock();
        updateBlockOn();
        saveLastPosition();
        updateVelocity();
        position = position.add( velocity );
        boundingBox.setRect( position.x - 15, position.y - 15, size, size );
        super.onUpdate();
    }

    public void onDeath() {
        dead = true;
        Map.setEnemyOff( position.x, position.y );
    }

    public void onRender() {
        double x = position.x;
        double y = position.y;
        glPushMatrix();
        {

            glTranslated( x, y, 0 );
            {
                glRotated( Math.toDegrees( facingAngle ), 0, 0, 1 );
                {
                    glColor3f( 1, 0, 0 );
                    ShapeRenderer.draw( new Circle( 0, 0, (float) this.radius ) );
                    ShapeRenderer.draw( new Line( 0, 0, (float) radius, 0 ) );
                }
            }
        }
        glPopMatrix();
    }

    public void updatePath() {

    }

    private void chooseBlock() {
        Block on = new Block( position.x, position.y );
        Block goTo = MapPathfinding.getBestChoice( on );
        if ( goTo == null ) {
            nextX = position.x;
            nextY = position.y;
        } else {
            nextX = goTo.x * Map.BLOCK_SIZE + Map.BLOCK_SIZE / 2;
            nextY = goTo.y * Map.BLOCK_SIZE + Map.BLOCK_SIZE / 2;
        }
    }

    private void updateVelocity() {
        double delta = Game.delta / 16.0;
        SVector2D acceleration = SVector2D.createZeroVector();
        acceleration = acceleration.add( poolForces() );
        facingAngle = Util.angleOfLineRad( position.x, position.y, nextX, nextY );
        acceleration = acceleration.add( SVector2D.createVectorGeometrically( facingAngle, speed ) );
        velocity = velocity.add( acceleration );
        acceleration = SVector2D.createZeroVector();
        acceleration = velocity.opposite().multiply( 0.3 );
        if ( velocity.magnitude - acceleration.magnitude >= 0 ) {
            velocity = velocity.add( acceleration );
        } else {
            velocity = SVector2D.createZeroVector();
        }
        collideWithWorld();
        position = position.add( velocity.multiply( delta ) );
    }

    private void saveLastPosition() {
        lastX = position.x;
        lastY = position.y;
    }

    private void updateBlockOn() {
        if ( Map.pointToID( lastX, lastY ) != Map.pointToID( position.x, position.y ) ) {
            //the block the enemy's on has changed
            Map.setEnemyOff( lastX, lastY );
        }
        Map.setEnemyOn( position.x, position.y );

    }

    public SVector2D getPosition() {
        return position;
    }


}
