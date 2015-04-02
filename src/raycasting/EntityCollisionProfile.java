package raycasting;

import entities.Entity;
import main.SVector2D;

import java.util.ArrayList;

public class EntityCollisionProfile implements Comparable<EntityCollisionProfile> {
    public final SVector2D positionClosest;
    public final double distanceClosest;
    public final SVector2D positionFurthest;
    public final Entity entity;

    EntityCollisionProfile( SVector2D positionClosest, SVector2D positionFurthest, Entity entity, double distanceClosest ) {
        this.distanceClosest = distanceClosest;
        this.positionClosest = positionClosest;
        this.positionFurthest = positionFurthest;
        this.entity = entity;
    }

    public static boolean doesProfileListContainTarget( ArrayList<EntityCollisionProfile> l, Entity a ) {
        for ( int i = 0; i < l.size(); i++ ) {
            if ( l.get( i ).entity == a ) {
                return true;
            }
        }
        return false;
    }

    public int compareTo( EntityCollisionProfile prof ) {
        if ( this.distanceClosest < prof.distanceClosest ) {
            return -1;
        } else if ( this.distanceClosest > prof.distanceClosest ) {
            return 1;
        }
        return 0;
    }
}