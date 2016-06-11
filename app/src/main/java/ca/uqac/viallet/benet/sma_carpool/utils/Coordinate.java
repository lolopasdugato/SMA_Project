package ca.uqac.viallet.benet.sma_carpool.utils;

/**
 * Created by Etienne on 2016-06-11.
 */
public class Coordinate {

    public int x;
    public int y;

    public Coordinate (int x , int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate (String s) {
        int num = 0;
        String[] values = s.split(",");
        this.x = Integer.parseInt(values[0]);
        this.y = Integer.parseInt(values[1]);
    }

    public String toString() {
        return x+","+y;
    }

    public int distance (Coordinate other) {
        return Math.abs(other.x - this.x) + Math.abs(other.y - this.y);
    }
}
