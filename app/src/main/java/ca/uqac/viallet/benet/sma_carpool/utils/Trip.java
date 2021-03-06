package ca.uqac.viallet.benet.sma_carpool.utils;

import java.util.ArrayList;

public class Trip {

    private ArrayList<Coordinate> route;

    private float detour;
    private float price;

    public float getDetour() {
        return detour;
    }

    public Trip(Coordinate dep, Coordinate arr) {
        route = new ArrayList<Coordinate>();
        route.add(dep);
        route.add(arr);
        detour = 0;
        price = 0;
    }

    public Trip(Coordinate dep, Coordinate arr, float detour, float price) {
        route = new ArrayList<Coordinate>();
        route.add(dep);
        route.add(arr);
        this.detour = detour;
        this.price = price;
    }

    public String toString() {
        StringBuilder routeStr = new StringBuilder();
        for (Coordinate coord : route) {
            routeStr.append(coord.toString());
            routeStr.append(' ');
        }
        routeStr.deleteCharAt(routeStr.length()-1);
        if (price != 0) {
            routeStr.append(" "+price);
        }
        return routeStr.toString();
    }

    public float routeLength() {
        float length = 0;
        for (int i = 0; i < route.size()-1; i++) {
            length += route.get(i).distance(route.get(i+1));
        }
        return length;
    }

    public float detourLength(Coordinate coord1, Coordinate coord2) {
        float min_length = Float.MAX_VALUE;
        int min_i = 0;
        int min_j = 0;
        ArrayList<Coordinate> routeCopy = new ArrayList<Coordinate>(route);

        for (int i = 1; i < routeCopy.size(); i++) {
            routeCopy.add(i, coord1);
            for (int j = i + 1; j < routeCopy.size(); j++) {
                routeCopy.add(j, coord2);

                float length = 0;
                for (int k = 0; k < routeCopy.size()-1; k++) {
                    length += routeCopy.get(k).distance(routeCopy.get(k+1));
                }

                if (length <= min_length) {
                    min_length = length;
                    min_i = i;
                    min_j = j;
                }
                routeCopy.remove(j);
            }
            routeCopy.remove(i);
        }
        return (min_length);
    }
}
