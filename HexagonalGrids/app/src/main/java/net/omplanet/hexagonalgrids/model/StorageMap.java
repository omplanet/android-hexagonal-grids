package net.omplanet.hexagonalgrids.model;

import android.util.Log;

import net.omplanet.hexagonalgrids.R;

/**
 * Storage for Hex nodes with column:q and row:r.
 * Sliding the rows to the left, and using variable sized rows to save space.
 * The access formula is array[r][q + r/2] if the grid is arranged in rectangular shape.
 * The access formula is array[r + radius][q + radius + min(0, r)] if arranged in hexagonal shape.
 */
public class StorageMap {
    private static final String TAG = StorageMap.class.getName();

    private int mRadius;
    private Grid.Shape mShape;
    private Object[][] mSquareMap;

    /**
     * Build a square grid that can cover the range of axial grid coordinates
     * @param radius The count of rings around the central node
     */
    public StorageMap(int radius, Grid.Shape shape, Object[][] squareMap) {
        mRadius = radius;
        mShape = shape;
        mSquareMap = squareMap;
    }

    //Slide the rows to the left, and use variable sized rows.
    public Object getObjectByCoordinate(int q, int r) {
        try {
            switch (mShape) {
                case HEXAGON_POINTY_TOP:
                    return mSquareMap[r + mRadius][q + mRadius + Math.min(0, r)];
                case RECTANGLE:
                    return mSquareMap[r][q];
            }
        } catch (Exception e) {
            Log.d(TAG, "IndexOutOfBound, the array element is not found for " + q + ":" + r);
        }

        return null;
    }
}
