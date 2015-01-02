package main;

import map.Map;

public class Block {
    public int x, y;

    public Block() {
        this.x = 0;
        this.y = 0;
    }

    public Block( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public Block( Block in ) {
        this.x = in.x;
        this.y = in.y;
    }

    public Block( double x, double y ) {
        this.x = (int) (x / Map.BLOCK_SIZE);
        this.y = (int) (y / Map.BLOCK_SIZE);
    }

    public boolean equals( Block in ) {
        return this.x == in.x && this.y == in.y;
    }
}
