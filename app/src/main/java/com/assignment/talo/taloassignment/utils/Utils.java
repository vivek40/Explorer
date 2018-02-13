package com.assignment.talo.taloassignment.utils;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;

/**
 * Created by vivek on 11/02/18.
 */

public class Utils {

    /**
     * Function to determine the number of columns to be displayed
     * in the recycler view based on the device width and density
     * @param context is the activity context
     * @return the count of the coulmns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    /**
     * Function to draw a circle using shapes
     * @param width is the width of the circle
     * @param height is the height of the circle
     * @param color is the color of the circle
     * @return the drawable version of the drawn circle
     */
    public static ShapeDrawable drawCircle (int width, int height, int color) {
        ShapeDrawable oval = new ShapeDrawable (new OvalShape());
        oval.setIntrinsicHeight (height);
        oval.setIntrinsicWidth (width);
        oval.getPaint ().setColor (color);
        return oval;
    }


}
