package model.structure.custom_structure;

import model.structure.structure_interface.DistanceMap;

import java.awt.*;

/**
 * Often in search algorithms there is a table that helps keep tracking of distances. This is the distance table that offers it.
 * Helper table to keep track of which nodes have been visited by search algorithms
 * Holds a single array and calculates points of interest with a function
 * To keep it lightweight no error checks are done.
 */
public class DistanceMapAsASingleTable implements DistanceMap {

    private double[] visitedStatusMap;
    private final int width;

    /**
     * Creates a double type array and instantiates all their values to integer.maxvalue
     * To keep it lightweight no error checks are done.
     *
     * @param height height of map
     * @param width  width of map
     */
    public DistanceMapAsASingleTable(int height, int width) {
        this.width = width;
        visitedStatusMap = new double[height * width];
        for (int i = 0; i < visitedStatusMap.length; i++) {
            visitedStatusMap[i] = Integer.MAX_VALUE;
        }
    }

    /**
     * To keep it lightweight no error checks are done.
     *
     * @param point point of interest
     * @return distance at that point
     */
    @Override
    public double getDistance(Point point) {
        return visitedStatusMap[point.y * width + point.x];
    }

    /**
     * To keep it lightweight no error checks are done.
     *
     * @param point  point where distance is set
     * @param weight distance value
     */
    @Override
    public void setDistance(Point point, double weight) {
        visitedStatusMap[point.y * width + point.x] = weight;
    }
}
