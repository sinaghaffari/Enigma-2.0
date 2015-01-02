package pathfinding;

import map.Map;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/*
 * Possible improvements:
 * -Precompute/save hscores
 * -have zombies follow stupider room-room paths when off screen and pf when on screen
 * -have zombies walk through walls when off screen,lol
 * -follow leader zombies
 * 
 */


//As of 8/22/12 no longer in use, A* is too slow for the number of zombies needed
//New pathfinding is integrated into map class

public class AStarPathFinding {

    public double targetX, targetY;
    private int[] parent = new int[Map.height * Map.length];
    private long[] fscore = new long[Map.height * Map.length];
    private long[] hscore = new long[Map.height * Map.length];
    private long[] gscore = new long[Map.height * Map.length];
    private int goal;
    private int current;
    private LinkedList<Integer> closed = new LinkedList<Integer>();
    private PriorityQueue<Integer> open = new PriorityQueue<Integer>( 100, new fscoreComparator() );


    public void findPath( int fromX, int fromY ) {
        long start = System.nanoTime();
        goal = Map.pointToID( 141 * 20, 141 * 20 );
        current = Map.blockToID( fromX, fromY );

        while ( current != goal ) {
            closed.push( current );
            addAdjacentSquares();
            if ( open.size() == 0 ) {
                return; //no path
            }

            current = open.poll();
        }
        targetX = current % Map.length;
        targetY = current / Map.height;

    }

    private void addAdjacentSquares() {
        addToOpen( current + 1 );
        addToOpen( current - 1 );
        addToOpen( current - Map.length );
        addToOpen( current + Map.length );
        //ifs are for bad diagonal paths
        if ( Map.isWalkable( current + 1 ) && Map.isWalkable( current + Map.length ) )
            addToOpen( current + 1 + Map.length );

        if ( Map.isWalkable( current - 1 ) && Map.isWalkable( current - Map.length ) )
            addToOpen( current - 1 - Map.length );

        if ( Map.isWalkable( current - Map.length ) && Map.isWalkable( current + 1 ) )
            addToOpen( current - Map.length + 1 );

        if ( Map.isWalkable( current + Map.length ) && Map.isWalkable( current - 1 ) )
            addToOpen( current + Map.length - 1 );
    }


    private void addToOpen( int to ) {
        if ( Map.isWalkable( to ) && !closed.contains( to ) ) {
            if ( !open.contains( to ) ) {
                updateScoresFor( to );
                open.add( to );
                parent[to] = current;
            } else if ( isDiagonal( to ) && gscore[current] + 14 < gscore[to] || gscore[current] + 10 < gscore[to] ) {
                updateScoresFor( to );
                open.remove( to );
                open.add( to );
                parent[to] = current;
            }


        }
    }

    private void updateScoresFor( int to ) {
        if ( isDiagonal( to ) )
            gscore[to] = gscore[current] + 14;
        else
            gscore[to] = gscore[current] + 10;

        if ( hscore[to] == 0 )//if it hasn't been found already yet
            hscore[to] = 10 * (Math.abs( goal % Map.length - to % Map.length ) + Math.abs( goal / Map.length - to / Map.length ));

        fscore[to] = gscore[to] + hscore[to];
    }

    private boolean isDiagonal( int to ) {
        return to == current - Map.length - 1 || to == current - Map.length + 1 || to == current + Map.length + 1 || to == current + Map.length - 1;
    }

    class fscoreComparator implements Comparator<Integer> {
        @Override
        public int compare( Integer b1, Integer b2 ) {
            if ( fscore[b1] < fscore[b2] )
                return -1;
            else
                return 1;
        }
    }
}


