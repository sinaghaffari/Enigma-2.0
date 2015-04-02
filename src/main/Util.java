package main;

import java.util.ArrayList;


public class Util {
    public static double angleOfLineRad( double x1, double y1, double x2, double y2 ) {
        double t = Math.atan2( (y2 - y1), (x2 - x1) );
        if ( t < 0 )
            return t + Math.PI * 2;
        else
            return t;
    }

    public static double angleOfLineDeg( double x1, double y1, double x2, double y2 ) {
        double t = Math.toDegrees( Math.atan2( (y2 - y1), (x2 - x1) ) );
        if ( t < 0 )
            return t + 360;
        else
            return t;
    }

    public static double distance( double x1, double y1, double x2, double y2 ) {
        return Math.sqrt( (Math.pow( x2 - x1, 2 ) + Math.pow( y2 - y1, 2 )) );
    }

    public static double distanceNotSquared( double x1, double y1, double x2, double y2 ) {
        return (Math.pow( x2 - x1, 2 ) + Math.pow( y2 - y1, 2 ));
    }

    public static int greatestCommonDivisor( int p, int q ) {
        if ( q == 0 ) {
            return p;
        }
        return greatestCommonDivisor( q, p % q );
    }

    public static ArrayList<Integer> findFactors( int num ) {
        int incrementer = 1;
        if ( num % 2 != 0 ) {
            incrementer = 2; //only test the odd ones
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = 1; i <= num / 2; i = i + incrementer ) {
            if ( num % i == 0 ) {
                list.add( i );
            }
        }
        list.add( num );
        return list;
    }

    public static int[] findMiddleFactors( int num ) {
        ArrayList<Integer> list = findFactors( num );
        if ( list.size() % 2 == 1 ) {
            return new int[]{ list.get( (int) Math.ceil( list.size() / 2.0 ) - 1 ), list.get( (int) Math.ceil( list.size() / 2.0 ) - 1 ) };
        } else {
            return new int[]{ list.get( (int) (list.size() / 2.0) - 1 ), list.get( (int) (list.size() / 2.0) ) };
        }
    }
}
