package com.assignment.talo.taloassignment.contract;

import android.content.Context;
import android.view.View;

import com.assignment.talo.taloassignment.ExploreData;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

/**
 * Created by vivek on 10/02/18.
 */

public interface ExploreListContract {
    public interface Presenter{
        void onCheckLocationPermission(Context context, FusedLocationProviderClient mFusedLocationClient);
        void onCreateDisplayFilterPopup(Context context, android.view.View clickedView);
        void onSortData(int selectedItem, List<ExploreData> exploreDataList);
    }

    public interface View{
        void onReceiveLocation(boolean isLocationAvailable, String location);
        void onPopUpItemSelected(int selectedPosition);
        void onSortedData();
    }

    public interface IFetchExploreList {
        void onSuccess(List<ExploreData> exploreDataList);
        void onFailure(String errorMessage);
    }

}
