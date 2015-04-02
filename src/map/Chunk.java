package map;

import entities.Entity;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Chunk {
    public int arrayX, arrayY;
    public Rectangle2D bounds = new Rectangle2D.Double();
    public ArrayList<Entity> entityList = new ArrayList<Entity>();
    public boolean doesContainWall = false;
    public ArrayList<Rectangle2D.Double> wallList = new ArrayList<Rectangle2D.Double>();

    public Chunk( int x, int y, double w, double h ) {
        double pixelW = w * Map.BLOCK_SIZE;
        double pixelH = h * Map.BLOCK_SIZE;
        double pixelX = x * pixelW;
        double pixelY = y * pixelH;
        arrayX = x;
        arrayY = y;
        bounds.setRect( pixelX, pixelY, pixelW, pixelH );
        for ( Rectangle2D.Double r : Map.newRectangles ) {
            if ( bounds.intersects( r ) ) {
                wallList.add( r );
                doesContainWall = true;
            }
        }
    }

    public void setChunk( int x, int y, double w, double h ) {
        double pixelW = w * Map.BLOCK_SIZE;
        double pixelH = h * Map.BLOCK_SIZE;
        double pixelX = x * pixelW;
        double pixelY = y * pixelH;
        arrayX = x;
        arrayY = y;
        bounds.setRect( pixelX, pixelY, pixelW, pixelH );
    }

    public void removeEntity( Entity e ) {
        entityList.remove( e );
    }

    public void addEntity( Entity e ) {
        entityList.add( e );
    }
}
