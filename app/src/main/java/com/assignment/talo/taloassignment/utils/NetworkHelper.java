package com.assignment.talo.taloassignment.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by vivek on 10/02/18.
 */

public class NetworkHelper {

    public static String SERVER_URL = "https://api.foursquare.com/v2/venues/explore?";
    public static String SERVER_GET_EXPLORE_LIST = "client_id="+Constants.CLIEND_ID+"&client_secret="+Constants.CLIEND_SECRET;


    private static RequestQueue mRequestQueue;

    public static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    /**
     * Function to check internet connectivity
     * @param context is the activity context
     * @return true if internet connection available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
