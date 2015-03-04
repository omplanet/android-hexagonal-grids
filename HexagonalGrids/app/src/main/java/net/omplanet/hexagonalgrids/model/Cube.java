package net.omplanet.hexagonalgrids.model;

/**
 * Cube using 3-vector for the coordinates (x, y, z)
 */
public class Cube {
    private int x;
    private int y;
    private int z;

    public Cube(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Cube(float x, float y, float z) {
        int rx = Math.round(x);
        int ry = Math.round(y);
        int rz = Math.round(z);

        float x_diff = Math.abs(rx - x);
        float y_diff = Math.abs(ry - y);
        float z_diff = Math.abs(rz - z);

        if (x_diff > y_diff && x_diff > z_diff)
            rx = -ry-rz;
        else if (y_diff > z_diff)
            ry = -rx-rz;
        else
            rz = -rx-ry;

        this.x = rx;
        this.y = ry;
        this.z = rz;
    }

    public Hex toHex() {
        return new Hex(x, z);
    }

    public Hex cubeToOddRHex() {
        int q = x + (z - (z&1)) / 2;
        int r = z;

        return new Hex(q, r);
    }

    public String toString() {
        return x + ":" + y + ":" + z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
