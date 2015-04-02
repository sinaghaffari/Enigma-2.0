package pathfinding;

import main.Block;
import main.Game;
import map.Map;

import java.util.LinkedList;
import java.util.Queue;

public class MapPathfinding {
    private static Queue<Block> toScore = new LinkedList<Block>();
    private static Block at;
    private static boolean[][] visited;
    private static Block bestTry = null;
    private static double bestScore = 998;//this value is important, walls are 999, if bestScore started as higher, enemies could walk on walls

    public static void update() {
        Block player = new Block( Game.getPlayerX(), Game.getPlayerY() );
        visited = new boolean[Map.height][Map.length];
        toScore.add( player );
        visited[player.y][player.x] = true;
        Map.scoredBlock[player.y][player.x] = 0;
        scoreBlocksForPathfinding();
    }

    private static void scoreBlocksForPathfinding() {

        while ( !toScore.isEmpty() ) {
            at = toScore.poll();

            addToQueue( -1, -1 );
            addToQueue( -1, 0 );
            addToQueue( 0, -1 );
            addToQueue( 1, -1 );
            addToQueue( -1, 1 );
            addToQueue( 1, 0 );
            addToQueue( 0, 1 );
            addToQueue( 1, 1 );

        }
    }

    private static void addToQueue( int plusX, int plusY ) {
        if ( !visited[at.y + plusY][at.x + plusX] && Map.isWalkable( at.x + plusX, at.y + plusY ) ) {
            toScore.add( new Block( at.x + plusX, at.y + plusY ) );
            visited[at.y + plusY][at.x + plusX] = true;
            Map.scoredBlock[at.y + plusY][at.x + plusX] = Map.scoredBlock[at.y][at.x] + 1;
        }
    }

    public static Block getBestChoice( Block on ) {
        bestTry = null;
        bestScore = 998;
        checkWith( on, -1, -1 );
        checkWith( on, -1, 0 );
        checkWith( on, 0, -1 );
        checkWith( on, 1, -1 );
        checkWith( on, -1, 1 );
        checkWith( on, 1, 0 );
        checkWith( on, 0, 1 );
        checkWith( on, 1, 1 );
        return bestTry;
    }

    private static void checkWith( Block on, int plusX, int plusY ) {
        double diagFix = 1;
        if ( plusX != 0 && plusY != 0 )
            diagFix = 1.2;
        if ( Map.scoredBlock[on.y + plusY][on.x + plusX] * diagFix < bestScore && Map.isWalkable( on.x + plusX, on.y + plusY ) ) {
            bestScore = Map.scoredBlock[on.y + plusY][on.x + plusX] * diagFix;
            bestTry = new Block( on.x + plusX, on.y + plusY );
        }
    }
}
