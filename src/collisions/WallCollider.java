package collisions;

import entities.Entity;
import main.SVector2D;
import main.Util;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class WallCollider {
    public static WallCollisionProfile collideEntityWithWalls( Entity entity, SVector2D direction, Rectangle2D.Double rec ) {
        WallCollisionProfile prof = new WallCollisionProfile();
        double wallBoundingPointX = 0, wallBoundingPointY = 0, playerPointingPointX = 0, playerPointingPointY = 0;
        double wallCollisionAngle;
        boolean shouldCollide = false;
        SVector2D force = null;
        int outcode;
        boolean redo = false;
        do {
            redo = false;
            outcode = rec.outcode( entity.position.x, entity.position.y );
            if ( outcode == 1 ) { // West
                wallBoundingPointX = rec.getMinX();
                wallBoundingPointY = entity.position.y;
                shouldCollide = (direction.x >= 0);
            } else if ( outcode == 3 ) { // North - West
                wallBoundingPointX = rec.getMinX();
                wallBoundingPointY = rec.getMinY();
                shouldCollide = (direction.x >= 0) || (direction.y >= 0);
            } else if ( outcode == 2 ) { // North
                wallBoundingPointX = entity.position.x;
                wallBoundingPointY = rec.getMinY();
                shouldCollide = (direction.y >= 0);
            } else if ( outcode == 6 ) { // North - East
                wallBoundingPointX = rec.getMaxX();
                wallBoundingPointY = rec.getMinY();
                shouldCollide = (direction.x <= 0) || (direction.y >= 0);
            } else if ( outcode == 4 ) { // East
                wallBoundingPointX = rec.getMaxX();
                wallBoundingPointY = entity.position.y;
                shouldCollide = (direction.x <= 0);
            } else if ( outcode == 12 ) { // South - East
                wallBoundingPointX = rec.getMaxX();
                wallBoundingPointY = rec.getMaxY();
                shouldCollide = (direction.x <= 0) || (direction.y <= 0);
            } else if ( outcode == 8 ) { // South
                wallBoundingPointX = entity.position.x;
                wallBoundingPointY = rec.getMaxY();
                shouldCollide = (direction.y <= 0);
            } else if ( outcode == 9 ) { // South - West
                wallBoundingPointX = rec.getMinX();
                wallBoundingPointY = rec.getMaxY();
                shouldCollide = (direction.x >= 0) || (direction.y <= 0);
            } else {
                SVector2D wallPosition = SVector2D.createVectorAlgebraically( rec.getCenterX(), rec.getCenterY() );
                double sx = rec.getMinX();
                double sy = rec.getMinY();
                double bx = rec.getMaxX();
                double by = rec.getMaxY();
                double x = entity.position.x;
                double y = entity.position.y;
                Line2D north = new Line2D.Double( sx, sy, bx, sy );
                Line2D east = new Line2D.Double( bx, sy, bx, by );
                Line2D south = new Line2D.Double( sx, by, bx, by );
                Line2D west = new Line2D.Double( sx, sy, sx, by );
                double distanceNorth = north.ptLineDist( x, y );
                double distanceEast = east.ptLineDist( x, y );
                double distanceSouth = south.ptLineDist( x, y );
                double distanceWest = west.ptLineDist( x, y );
                double closest = distanceNorth;
                if ( distanceEast < closest ) closest = distanceEast;
                if ( distanceSouth < closest ) closest = distanceSouth;
                if ( distanceWest < closest ) closest = distanceWest;

                double s = 0, a = 0;
                if ( closest == distanceNorth ) {
                    wallPosition = SVector2D.createVectorAlgebraically( entity.position.x, rec.getCenterY() );
                    s = rec.height / 2.0 + entity.radius;
                    a = Math.toRadians( 270 );
                } else if ( closest == distanceEast ) {
                    wallPosition = SVector2D.createVectorAlgebraically( rec.getCenterX(), entity.position.y );
                    s = rec.width / 2.0 + entity.radius;
                    a = Math.toRadians( 0 );
                } else if ( closest == distanceSouth ) {
                    wallPosition = SVector2D.createVectorAlgebraically( entity.position.x, rec.getCenterY() );
                    s = rec.height / 2.0 + entity.radius;
                    a = Math.toRadians( 90 );
                } else if ( closest == distanceWest ) {
                    wallPosition = SVector2D.createVectorAlgebraically( rec.getCenterX(), entity.position.y );
                    s = rec.width / 2.0 + entity.radius;
                    a = Math.toRadians( 180 );
                }
                entity.position = wallPosition.add( SVector2D.createVectorGeometrically( a, s ) );
                redo = true;
            }
        } while ( redo );
        playerPointingPointX = wallBoundingPointX + (15 * Math.cos( Util.angleOfLineRad( wallBoundingPointX, wallBoundingPointY, entity.position.x, entity.position.y ) ));
        playerPointingPointY = wallBoundingPointY + (15 * Math.sin( Util.angleOfLineRad( wallBoundingPointX, wallBoundingPointY, entity.position.x, entity.position.y ) ));
        if ( Util.distance( wallBoundingPointX, wallBoundingPointY, entity.position.x, entity.position.y ) <= Util.distance( wallBoundingPointX, wallBoundingPointY, playerPointingPointX, playerPointingPointY ) ) {
            if ( shouldCollide || (direction.x == 0 && direction.y == 0) ) {
                wallCollisionAngle = Util.angleOfLineRad( entity.position.x, entity.position.y, wallBoundingPointX, wallBoundingPointY );
                SVector2D unitTowardsWall = SVector2D.createVectorGeometrically( wallCollisionAngle, 1 );
                SVector2D wallComponent = entity.velocity.projectOnto( unitTowardsWall );
                force = wallComponent.opposite();
                prof.add( playerPointingPointX, playerPointingPointY, force, outcode );
                return prof;
            }
        }
        return null;
    }
}

