package mapGenerator;

import model.WebMap;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Simple wightless map generator that asks user for parameters for generating a map.
 */
public class NoWeightSimpleGenerator implements MapGenerator {
    protected final Scanner scanner;
    private int mapWidth, mapHeight;
    private Point mapStartLocation;
    private Point mapTargetLocation;
    private String mapName = "nameless";

    /**
     *
     * @param scanner used to interact with user for configuring values.
     */
    public NoWeightSimpleGenerator(Scanner scanner) {
        this.scanner = scanner;
    }


    /**
     * Asks user input values to create a map. And will loop forever until valid values are given.
     * Values requird by user:
     * width over 0
     * height over 0
     * tile start x coordinate within 0 and width - 1
     * tile start y coordinate within 0 and height - 1
     * tile target x coordinate within 0 and width - 1
     * tile target y coordinate within 0 and height - 1
     * @return a valid map
     *
     */
    @Override
    public WebMap createMap() {
        setConfigValues();
        WebMap map = new WebMap();
        map.setName(mapName);
        map.setMap(generateTiles());
        map.setTileTarget(mapTargetLocation);
        map.setTileStart(mapStartLocation);
        return  map;
    }

    private int[][] generateTiles() {
        int[][] map = new int[mapHeight][mapWidth];
        for (int[] row : map) {
            Arrays.fill(row, 1);
        }
        return map;
    }

    private void setConfigValues() {
        while (true) {
            try {
                int startX, startY, targetX, targetY;
                System.out.println("set width: ");
                mapWidth =Integer.parseInt(scanner.nextLine());
                System.out.println("set height: ");
                mapHeight = Integer.parseInt(scanner.nextLine());
                System.out.println("set starting location x");
                startX = Integer.parseInt(scanner.nextLine());
                System.out.println("set starting location y");
                startY = Integer.parseInt(scanner.nextLine());
                System.out.println("set target location x");
                targetX = Integer.parseInt(scanner.nextLine());
                System.out.println("set target location y");
                targetY = Integer.parseInt(scanner.nextLine());

                if (startY < 0 || startX < 0 || targetX < 0 || targetY < 0) throw new ArrayIndexOutOfBoundsException();
                if (startY >= mapHeight || startX >= mapWidth || targetX >= mapWidth || targetY >= mapHeight) throw new ArrayIndexOutOfBoundsException();
                if (mapHeight <= 0 || mapWidth <= 0) throw new IllegalArgumentException();
                mapStartLocation = new Point(startX, startY);
                mapTargetLocation = new Point(targetX, targetY);
                return;
            } catch (Exception e) {
                System.out.println("width and height should be between 1-" + Integer.MAX_VALUE + ". x,y values should be between 0-" + (Integer.MAX_VALUE - 1));
            }
        }
    }

    @Override
    public String toString() {
        return "simple weightless and walless map generator";
    }

}
