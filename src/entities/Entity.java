package entities;

import bullets.StandardBullet;
import collisions.WallCollider;
import collisions.WallCollisionProfile;
import guns.Gun;
import main.Game;
import main.SVector2D;
import main.Util;
import map.Chunk;
import map.ChunkManager;
import org.lwjgl.input.Keyboard;
import raycasting.RayCollisionProfile;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Entity {
    public static final double G = 6.67300E2;
    public ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
    public byte entityType;
    public SVector2D position = SVector2D.createZeroVector();
    public SVector2D velocity = SVector2D.createZeroVector();
    public SVector2D momentum = SVector2D.createZeroVector();
    public double radius = 15;
    public double facingAngle;
    public double health;
    public Rectangle2D boundingBox = new Rectangle2D.Double();
    public boolean dead = false;
    public ArrayList<Gun> guns = new ArrayList<Gun>();
    public int currentGun;
    public boolean isTriggerDown;
    public double gunRecoilBeingFelt;
    public double totalRecoilBeingFelt;
    public boolean entityShake;
    public ArrayList<SVector2D> forces = new ArrayList<SVector2D>();
    public boolean isFixed = false;
    double turningRecoilBeingFelt;
    double movementRecoilBeingFelt;
    double ambientRecoilBeingFelt;
    double entityShakeRecoil;
    boolean onAWall = false;
    double entityRadius = 15;

    public SVector2D poolForces() {
        SVector2D temp = SVector2D.createZeroVector();
        for ( SVector2D vec : forces ) {
            temp = temp.add( vec );
        }
        forces.clear();
        return temp;
    }

    public void addForce( SVector2D vec ) {
        forces.add( vec );
    }

    public void collideWithWorld() {
        boolean shouldCheckWalls = false;
        Set<Rectangle2D.Double> wallSet = new HashSet<Rectangle2D.Double>();
        for ( Chunk c : this.chunkList ) {
            if ( c.doesContainWall ) {
                shouldCheckWalls = true;
                wallSet.addAll( c.wallList );
            }
        }
        ArrayList<Rectangle2D.Double> wallList = new ArrayList<Rectangle2D.Double>( wallSet );
        if ( shouldCheckWalls ) {
            for ( Rectangle2D.Double rec : wallList ) {
                WallCollisionProfile prof = WallCollider.collideEntityWithWalls( this, velocity, rec );
                if ( prof != null ) {
                    position = SVector2D.createVectorAlgebraically( prof.playerPointingPointX, prof.playerPointingPointY );
                }
            }
        }
        Set<Entity> entitySet = new HashSet<Entity>();
        ArrayList<Chunk> chunkList = ChunkManager.whichChunks( boundingBox );
        for ( Chunk c : chunkList ) {
            entitySet.addAll( c.entityList );
        }
        ArrayList<Entity> entityList = new ArrayList<Entity>( entitySet );
        for ( Entity e : entityList ) {
            if ( e != this ) {
                double distance = Util.distance( position.x, position.y, e.position.x, e.position.y );
                double m1 = 1;
                double m2 = 1;
                SVector2D midpoint = null;
                double angleFromMidToThis = 0;
                double angleFromMidToE = 0;
                double angleFromThisToE = 0;
                double angleFromEToThis = 0;
                if ( distance < e.entityRadius + this.entityRadius || Keyboard.isKeyDown( Keyboard.KEY_RETURN ) ) {
                    m1 = 1;
                    m2 = 1;
                    midpoint = SVector2D.createVectorAlgebraically( (position.x + e.position.x) / 2, (position.y + e.position.y) / 2 );
                    angleFromMidToThis = Util.angleOfLineRad( midpoint.x, midpoint.y, position.x, position.y );
                    angleFromMidToE = Util.angleOfLineRad( midpoint.x, midpoint.y, e.position.x, e.position.y );
                    angleFromThisToE = Util.angleOfLineRad( position.x, position.y, e.position.x, e.position.y );
                    angleFromEToThis = Util.angleOfLineRad( e.position.x, e.position.y, position.x, position.y );
                }
                if ( distance < e.entityRadius + this.entityRadius ) {

                    // Calculate new positions
                    if ( this.isFixed ) {
                        e.position = this.position.add( SVector2D.createVectorGeometrically( angleFromThisToE, e.radius + this.radius ) );
                    } else if ( e.isFixed ) {
                        this.position = e.position.add( SVector2D.createVectorGeometrically( angleFromEToThis, e.radius + this.radius ) );
                    } else {
                        position = midpoint.add( SVector2D.createVectorGeometrically( angleFromMidToThis, this.radius ) );
                        e.position = midpoint.add( SVector2D.createVectorGeometrically( angleFromMidToE, e.radius ) );
                    }
                    SVector2D u = SVector2D.createVectorGeometrically( angleFromThisToE, 1 );

                    if ( u.dot( this.velocity ) > 0 ) {
                        if ( this.isFixed ) {
                            SVector2D unitInThisDirection = SVector2D.createVectorGeometrically( angleFromEToThis, 1 );
                            e.addForce( e.velocity.projectOnto( unitInThisDirection ).opposite() );
                        } else if ( e.isFixed ) {
                            SVector2D unitInEDirection = SVector2D.createVectorGeometrically( angleFromThisToE, 1 );
                            addForce( velocity.projectOnto( unitInEDirection ).opposite() );
                        } else {
                            distance = Util.distance( position.x, position.y, e.position.x, e.position.y );
                            SVector2D n = position.subtract( e.position ).divide( distance );

                            double p = (2 * (velocity.dot( n ) - e.velocity.dot( n ))) / (m1 + m2);
                            velocity = velocity.subtract( n.multiply( p ).multiply( m1 ) );
                            e.velocity = e.velocity.add( n.multiply( p ).multiply( m2 ) );
                        }
                    }
                }
            }
        }
        if ( shouldCheckWalls ) {
            for ( Rectangle2D.Double rec : wallList ) {
                WallCollisionProfile prof = WallCollider.collideEntityWithWalls( this, velocity, rec );
                if ( prof != null ) {
                    position = SVector2D.createVectorAlgebraically( prof.playerPointingPointX, prof.playerPointingPointY );
                    velocity = velocity.add( prof.force );
                }
            }
        }

    }

    public void onUpdate() {
        if ( Game.gravity )
            velocity = velocity.add( SVector2D.createVectorAlgebraically( 0, 0.098 ) );
        checkChunks();
        if ( health < 0 ) {
            this.onDeath();
        }

    }

    public void checkChunks() {
        ChunkManager.updateEntityChunks( this );
    }

    public void onRender() {

    }

    public void onDeath() {
        dead = true;
    }

    public void onHitWithBullet( StandardBullet bullet, RayCollisionProfile profile ) {
        addForce( SVector2D.createVectorGeometrically( bullet.angle, 1 ).projectOnto( SVector2D.createVectorGeometrically( Util.angleOfLineRad( profile.closestPosition.x, profile.closestPosition.y, this.position.x, this.position.y ), 1 ) ) );
        health -= bullet.parentGun.damage;
    }
}

