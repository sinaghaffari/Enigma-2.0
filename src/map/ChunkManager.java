package map;

import entities.Entity;
import main.SVector2D;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.ShapeRenderer;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

public class ChunkManager {
    public static Chunk[][] chunkArray;
    public static ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
    public static int chunkWidth, chunkHeight;

    public static void initializeChunkManager( int x, int y, int w, int h ) {
        chunkWidth = w * Map.BLOCK_SIZE;
        chunkHeight = h * Map.BLOCK_SIZE;
        chunkArray = new Chunk[x][y];
        for ( int a = 0; a < chunkArray.length; a++ ) {
            for ( int b = 0; b < chunkArray[0].length; b++ ) {
                chunkArray[a][b] = new Chunk( a, b, w, h );
                chunkList.add( chunkArray[a][b] );
            }
        }
    }

    public static ArrayList<Chunk> whichChunks( Rectangle2D d ) {
        Set<Chunk> list = new HashSet<Chunk>();
        int x1, y1, x2, y2, w, h;
        x1 = (int) Math.floor( d.getMinX() / chunkWidth );
        y1 = (int) Math.floor( d.getMinY() / chunkHeight );
        x2 = (int) Math.floor( d.getMaxX() / chunkWidth );
        y2 = (int) Math.floor( d.getMaxY() / chunkHeight );
        w = x2 - x1;
        h = y2 - y1;
        for ( int x = 0; x <= w; x++ ) {
            for ( int y = 0; y <= h; y++ ) {
                int a = x1 + x;
                int b = y1 + y;
                int mw = ChunkManager.chunkArray.length;
                int mh = ChunkManager.chunkArray[0].length;
                if ( a >= 0 && a < mw && b >= 0 && b < mh ) {
                    list.add( chunkArray[a][b] );
                }
            }
        }
        return new ArrayList<Chunk>( list );
    }

    public static Chunk whichChunk( SVector2D pos ) {
        int x, y;
        x = (int) Math.floor( pos.x / chunkWidth );
        y = (int) Math.floor( pos.y / chunkHeight );
        return chunkArray[x][y];
    }

    public static void addEntity( Entity e ) {
        ArrayList<Chunk> cList = whichChunks( e.boundingBox );

        for ( Chunk c : cList ) {
            chunkArray[c.arrayX][c.arrayY].entityList.add( e );
        }
        e.chunkList = (ArrayList<Chunk>) cList.clone();

    }

    public static void updateEntityChunks( Entity e ) {
        ArrayList<Chunk> oldCList = (ArrayList<Chunk>) e.chunkList.clone();
        ArrayList<Chunk> cList = whichChunks( e.boundingBox );

        for ( Chunk c : oldCList ) {
            chunkArray[c.arrayX][c.arrayY].entityList.remove( e );
        }
        for ( Chunk c : cList ) {
            chunkArray[c.arrayX][c.arrayY].entityList.add( e );
        }
        e.chunkList = (ArrayList<Chunk>) cList.clone();
    }

    public static void removeEntity( Entity e ) {
        ArrayList<Chunk> cList = whichChunks( e.boundingBox );

        for ( Chunk c : cList ) {
            chunkArray[c.arrayX][c.arrayY].entityList.remove( e );
        }
    }

    public static void onRender() {
        glPushMatrix();
        for ( int a = 0; a < chunkArray.length; a++ ) {
            for ( int b = 0; b < chunkArray[0].length; b++ ) {
                Rectangle2D r = chunkArray[a][b].bounds;
                glColor4f( 1, 1, 1, 1 );
                ShapeRenderer.draw( new Rectangle( (float) r.getMinX(), (float) r.getMinY(), (float) r.getWidth(), (float) r.getHeight() ) );
            }
        }
        glPopMatrix();
    }
}
