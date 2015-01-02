package entities;

import map.ChunkManager;

import java.util.ArrayList;

public class EntityManager {
    public static ArrayList<Entity> globalEntityList = new ArrayList<Entity>();

    public static void addEntity( Entity e ) {
        globalEntityList.add( e );
        ChunkManager.addEntity( e );
    }

    public static void removeEntity( Entity e ) {
        globalEntityList.remove( e );
        ChunkManager.removeEntity( e );
    }

}
