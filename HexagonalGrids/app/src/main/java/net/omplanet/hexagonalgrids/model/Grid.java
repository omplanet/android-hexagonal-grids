package net.omplanet.hexagonalgrids.model;

import android.graphics.Point;

/**
        * *   * *   *
       *   * *   * *
        * *   * *   *
       *   * *   * *
        * *   * *   *
 * A grid of hex nodes with axial coordinates.
 */
 public class Grid {
    public enum Shape {
        RECTANGLE,
        HEXAGON_POINTY_TOP
    }

    public final int radius; //The radius of the grid - the count of rings around the central node
    public final int scale; //The radius of the single node in grid
    public final Shape shape; //The shape of the grid

    //Derived node properties
    public final int width; //The width of the single hexagon node
    public final int height; //The height of the single hexagon node
    public final int centerOffsetX; //Relative center coordinate within one node
    public final int centerOffsetY; //Relative center coordinate within one node

    public Cube[] nodes;

    /**
     * Construing a Grid with a set of cubes, scale, and shape
     * @param radius The count of rings around the central node
     * @param scale The radius of the hexagon in pixels
     * @param shape The shape of the hexagon
     */
    public Grid(int radius, int scale, Shape shape) {
        this.radius = radius;
        this.scale = scale;
        this.shape = shape;

        //Init derived node properties
        width = (int) (Math.sqrt(3) * scale);
        height = 2 * scale;
        centerOffsetX = width/2;
        centerOffsetY = height/2;

        //Init nodes
        switch (shape) {
            case HEXAGON_POINTY_TOP:
                generateHexagonalShape(radius);
                break;
            case RECTANGLE:
                generateRectangleShape(radius);
                break;
        }
    }

    public Point hexToPixel(Hex hex) {
        int x = 0;
        int y = 0;

        switch (shape) {
            case HEXAGON_POINTY_TOP:
                x = (int) (width * (hex.getQ() + 0.5 * hex.getR()));
                y = (int) (scale * 1.5 * hex.getR());
                break;
            case RECTANGLE:
                //oddR alignment
                x = (int) (width * hex.getQ() + 0.5 * width * (hex.getR()%2));
                y = (int) (scale * 1.5 * hex.getR());
                break;
        }

        return new Point(x, y);
    }

    public Hex pixelToHex(float x, float y) {
        float q = (float) (Math.sqrt(3)/3 * x - 1/3 * y) / scale;
        float r = (2/3 * y) / scale;

        return new Hex(q, r);

        //TODO RECTANGLE
    }

    private void generateHexagonalShape(int radius) throws ArrayIndexOutOfBoundsException {
        nodes = new Cube[getNumberOfNodesInGrid(radius, shape)];
        int i = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                int z = -x-y;
                if (Math.abs(x) <= radius && Math.abs(y) <= radius && Math.abs(z) <= radius) {
                    nodes[i++] = new Cube(x, y, z);
                }
            }
        }
    }

    private void generateRectangleShape(int radius) {
        int minQ=0;
        int maxQ=radius*2;
        int minR=0;
        int maxR=radius*2;

        nodes = new Cube[getNumberOfNodesInGrid(radius, shape)];
        int i = 0;

        for (int q = minQ; q <= maxQ; q++) {
            for (int r = -minR; r <= maxR; r++) {
                nodes[i++] = new Hex(q,r).oddRHexToCube(); //conversion to cube is different for oddR coordinates
            }
        }
    }

    /**
     * @return Number of hexagons inside of a hex or oddR rectangle shaped grid with the given radius
     */
    public static int getNumberOfNodesInGrid(int radius, Shape shape) {
        switch (shape) {
            case HEXAGON_POINTY_TOP:
                return (int) (3 * Math.pow(radius+1, 2) - 3 * (radius +1) + 1);
            case RECTANGLE:
                return (radius * 2 + 1) * (radius * 2 + 1);
        };

        return 0;
    }

    public static int getGridWidth(int radius, int scale, Shape shape) {
        switch (shape) {
            case HEXAGON_POINTY_TOP:
                return (int) ((2*radius + 1) * Math.sqrt(3) * scale);
            case RECTANGLE:
                return 0; //TODO
        };

        return 0;
    }

    public static int getGridHeight(int radius, int scale, Shape shape) {
        switch (shape) {
            case HEXAGON_POINTY_TOP:
                return (int) (scale * ((2*radius + 1) * 1.5 + 0.5));
            case RECTANGLE:
                return 0; //TODO
        };

        return 0;
    }
}
