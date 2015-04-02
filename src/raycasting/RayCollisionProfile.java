package raycasting;

import main.SVector2D;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class RayCollisionProfile {
    public final SVector2D wallPosition;
    public final ArrayList<EntityCollisionProfile> entityCollisions;
    public final SVector2D closestPosition;
    public final Rectangle2D.Double rec;
    public final SVector2D faceReferenceVector;

    RayCollisionProfile( SVector2D wallPosition, SVector2D closestPosition, ArrayList<EntityCollisionProfile> entityCollisions, Rectangle2D.Double rec, SVector2D faceReferenceVector ) {
        this.wallPosition = wallPosition;
        this.entityCollisions = entityCollisions;
        this.closestPosition = closestPosition;
        this.rec = rec;
        this.faceReferenceVector = faceReferenceVector;
    }
}