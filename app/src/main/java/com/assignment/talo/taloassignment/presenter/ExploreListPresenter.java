package com.assignment.talo.taloassignment.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.assignment.talo.taloassignment.ExploreData;
import com.assignment.talo.taloassignment.ui.ExploreListActivity;
import com.assignment.talo.taloassignment.contract.ExploreListContract;
import com.assignment.talo.taloassignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vivek on 10/02/18.
 */

public class ExploreListPresenter implements ExploreListContract.Presenter {

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    ExploreListContract.View mExploreListContractView;

    public ExploreListPresenter(ExploreListContract.View view) {
        this.mExploreListContractView = view;
    }


    @Override
    public void onCheckLocationPermission(Context context, FusedLocationProviderClient mFusedLocationClient) {
        if (!checkPermissions(context)) {
            requestPermissions((Activity) context);
        } else {
            getLastLocation((Activity) context, mFusedLocationClient);
        }
    }

    /**
     * Provides a simple way of getting a device's distanceText and is well suited for
     * applications that do not require a fine-grained distanceText and that do not need distanceText
     * updates. Gets the best and most recent distanceText currently available, which may be null
     * in rare cases when a distanceText is not available.
     * <p>
     * Note: this method should be called after distanceText permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    public void getLastLocation(final Activity activity, final FusedLocationProviderClient mFusedLocationClient) {

        mFusedLocationClient.getLastLocation().addOnCompleteListener(activity, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location mLastLocation = task.getResult();

                    mExploreListContractView.onReceiveLocation(true, mLastLocation.getLatitude() + "," +
                            mLastLocation.getLongitude());
                } else {
                    mExploreListContractView.onReceiveLocation(false, activity.getString(R.string.no_location_detected));
                    setLocationRequest(activity, mFusedLocationClient);
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void setLocationRequest(Activity activity, final FusedLocationProviderClient mFusedLocationClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        if (isLocationEnabled(activity)) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mExploreListContractView.onReceiveLocation(true, locationResult.getLastLocation().getLatitude() + "," +
                            locationResult.getLastLocation().getLongitude());

                    mFusedLocationClient.removeLocationUpdates(this);
                }
            }, null);
        }
    }


    /**
     * Function to check if location is enabled or not
     *
     * @param context is the activity context
     * @return true if location is on else false
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions(Context context) {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Function to request the location permission
     *
     * @param activity is the current activity
     */
    private void startLocationPermissionRequest(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ExploreListActivity.REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Function to check is permission is available or not
     *
     * @param context is the activity context
     */
    private void requestPermissions(final Activity context) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            showSnackbar(context, R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest(context);
                        }
                    });

        } else {
            startLocationPermissionRequest(context);
        }
    }

    @Override
    public void onSortData(final int selectedOption, List<ExploreData> exploreDataList) {
        Collections.sort(exploreDataList, new Comparator<ExploreData>() {
            @Override
            public int compare(ExploreData exploreData, ExploreData t1) {
                if (selectedOption == ExploreListActivity.POP_UP_MENU_ITEM_DISTANCE) {
                    return exploreData.getDistance() - t1.getDistance();
                } else if (selectedOption == ExploreListActivity.POP_UP_MENU_ITEM_CATEGORY) {
                    return exploreData.getCategory().compareTo(t1.getCategory());
                } else {
                    return Float.compare(t1.getRating(), exploreData.getRating());
                }
            }
        });
        mExploreListContractView.onSortedData();
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    public void showSnackbar(Activity activity, final int mainTextStringId, final int actionStringId,
                             View.OnClickListener listener) {
        Snackbar.make(activity.findViewById(android.R.id.content),
                activity.getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(activity.getString(actionStringId), listener).show();
    }


    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    public void showSnackbar(Activity activity, final String text) {
        View container = activity.findViewById(R.id.main_layout);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateDisplayFilterPopup(Context context, View clickedView) {
        PopupMenu menu = new PopupMenu(context, clickedView);
        menu.getMenu().add(Menu.NONE, ExploreListActivity.POP_UP_MENU_ITEM_DISTANCE, ExploreListActivity.POP_UP_MENU_ITEM_DISTANCE, context.getString(R.string.distance));
        menu.getMenu().add(Menu.NONE, ExploreListActivity.POP_UP_MENU_ITEM_CATEGORY, ExploreListActivity.POP_UP_MENU_ITEM_CATEGORY, context.getString(R.string.category));
        menu.getMenu().add(Menu.NONE, ExploreListActivity.POP_UP_MENU_ITEM_RATING, ExploreListActivity.POP_UP_MENU_ITEM_RATING, context.getString(R.string.rating));
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                mExploreListContractView.onPopUpItemSelected(i);
                return true;
            }

        });
    }
}
