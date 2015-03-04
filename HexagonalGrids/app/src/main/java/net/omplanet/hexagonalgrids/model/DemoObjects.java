package net.omplanet.hexagonalgrids.model;

import net.omplanet.hexagonalgrids.R;

/**
 * Demo contents.
 */
public class DemoObjects {
    //int size = mRadius*2+1;
    //squareMap = new Integer[size][size];
    public static Object[][] squareMap = new Integer[][] {
        {
            R.drawable.profile1, 0, R.drawable.profile2, R.drawable.profile3},
        {R.drawable.profile4, 0, R.drawable.profile5, R.drawable.profile6, R.drawable.profile7,},
        {0, R.drawable.profile8, R.drawable.profile9, R.drawable.profile10, R.drawable.profile11, R.drawable.profile12},
        {0, 0, 0, null, 0, 0, 0},
        {0, 0, 0, 0, 0, 0},
        {0, R.drawable.profile13, R.drawable.profile14, R.drawable.profile15, 0},
        {null, 0, 0, null}
    };
}
