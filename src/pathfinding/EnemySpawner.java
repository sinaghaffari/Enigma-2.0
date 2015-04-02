package pathfinding;

import entities.EntityEnemy;
import entities.EntityManager;
import main.Game;
import map.Map;

public class EnemySpawner {

    public static int counter = 0;
    private static int waveNumber;
    private static int numToSpawn;
    private static int maxOnScreen;
    private static int spawnX, spawnY;
    private static EntityEnemy currentEnemy = null;

    public static void initializeSpawner() {
        waveNumber = 1;
        numToSpawn = 1000000000;
        maxOnScreen = 50;
    }

    public static void update() {
        if ( timeIsGood() && numToSpawn != 0 )
            spawnEnemy();
    }

    private static boolean timeIsGood() {
        return (int) (Math.random() * 60) == 1;
    }

    private static void spawnEnemy() {
        findGoodPosition();
        chooseZombType();

        EntityManager.addEntity( currentEnemy );
        currentEnemy = null;
        numToSpawn--;

    }

    private static void findGoodPosition() {
        do {
            spawnX = (int) (Math.random() * Map.length); //not that efficient, will probably make better algorithm for finding empty spot
            spawnY = (int) (Math.random() * Map.height);

        } while ( !Map.isWalkable( spawnX, spawnY ) || Game.isOnScreen( spawnX * 20, spawnY * 20 ) );

    }

    private static double distanceFromPlayer() {
        return Math.sqrt( Math.pow( spawnX * Map.BLOCK_SIZE - Game.getPlayerX(), 2 ) + Math.pow( spawnY * Map.BLOCK_SIZE - Game.getPlayerY(), 2 ) );
    }

    private static void chooseZombType() {
        currentEnemy = new EntityEnemy( spawnX, spawnY ); //will be updated when subclasses are implemented
    }

}
