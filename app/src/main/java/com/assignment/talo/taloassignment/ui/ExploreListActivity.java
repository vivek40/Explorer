package com.assignment.talo.taloassignment.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assignment.talo.taloassignment.BuildConfig;
import com.assignment.talo.taloassignment.ExploreAdapter;
import com.assignment.talo.taloassignment.ExploreData;
import com.assignment.talo.taloassignment.R;
import com.assignment.talo.taloassignment.contract.ExploreListContract;
import com.assignment.talo.taloassignment.presenter.ExploreListPresenter;
import com.assignment.talo.taloassignment.utils.Constants;
import com.assignment.talo.taloassignment.utils.ExploreListFetcher;
import com.assignment.talo.taloassignment.utils.NetworkHelper;
import com.assignment.talo.taloassignment.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreListActivity extends AppCompatActivity implements ExploreListContract.View, ExploreAdapter.ItemClickListener{


    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final int POP_UP_MENU_ITEM_DISTANCE = 1;
    public static final int POP_UP_MENU_ITEM_CATEGORY = 2;
    public static final int POP_UP_MENU_ITEM_RATING = 3;

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private ExploreListPresenter mExploreListPresenter;

    RecyclerView exploreGridView;
    TextView totalItemsCount, filterItems, enableLocationText;
    Button enableLocationBtn;
    ExploreAdapter exploreAdapter;
    boolean isAddressVisible = false, isContactVisible = false;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mExploreListPresenter = new ExploreListPresenter(this);
        mExploreListPresenter.onCheckLocationPermission(ExploreListActivity.this, mFusedLocationClient);
    }

    /**
     * Function to initilize the views
     */
    private void setupViews(){
        exploreGridView = (RecyclerView) findViewById(R.id.explore_grid);
        totalItemsCount = (TextView) findViewById(R.id.total_items);
        filterItems = (TextView) findViewById(R.id.filter_items);
        enableLocationText = (TextView) findViewById(R.id.enable_text);
        enableLocationBtn = (Button) findViewById(R.id.enable_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        enableLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExploreListPresenter.onCheckLocationPermission(ExploreListActivity.this, mFusedLocationClient);
            }
        });
    }

    @Override
    public void onReceiveLocation(boolean isLocationAvailable, String location) {

        if(isLocationAvailable){

            enableLocationText.setVisibility(View.GONE);
            enableLocationBtn.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            if(NetworkHelper.isNetworkAvailable(this)) {

                ExploreListFetcher exploreListFetcher = new ExploreListFetcher();
                exploreListFetcher.getExploreList(ExploreListActivity.this, location, new ExploreListContract.IFetchExploreList() {
                    @Override
                    public void onSuccess(final List<ExploreData> exploreDataList) {
                        setupAdapter(exploreDataList);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        enableDisableViews(errorMessage);
                        mExploreListPresenter.showSnackbar(ExploreListActivity.this, errorMessage);
                    }
                });
            }else
                enableDisableViews(getString(R.string.internet_error));

        }else
            enableDisableViews(location);

    }

    /**
     * Function to display error message and make it visible
     * @param errorMessage is the content to be displayed
     */
    private void enableDisableViews(String errorMessage){
        mProgressBar.setVisibility(View.GONE);
        enableLocationText.setVisibility(View.VISIBLE);
        enableLocationBtn.setVisibility(View.VISIBLE);
        enableLocationText.setText(errorMessage);
        enableLocationBtn.setText(getString(R.string.try_again));
    }

    /**
     * Function to set the recyler adapter and load the data
     * @param exploreDataList is the list of data
     */
    private void setupAdapter(List<ExploreData> exploreDataList){
        mProgressBar.setVisibility(View.GONE);

        if(exploreDataList.size() > 0) {
            totalItemsCount.setVisibility(View.VISIBLE);
            filterItems.setVisibility(View.VISIBLE);

            totalItemsCount.setText(exploreDataList.size() + getString(R.string.items));
            int mNoOfColumns = Utils.calculateNoOfColumns(getApplicationContext());

            exploreAdapter = new ExploreAdapter(exploreDataList);
            exploreGridView.setLayoutManager(new GridLayoutManager(ExploreListActivity.this, mNoOfColumns));
            exploreAdapter.setClickListener(this);
            exploreGridView.setAdapter(exploreAdapter);

            filterItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExploreListPresenter.onCreateDisplayFilterPopup(ExploreListActivity.this, view);
                }
            });

            invalidateOptionsMenu();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        ExploreDetailDialog newFragment = ExploreDetailDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.SELECTED_ITEM, exploreAdapter.getExploreDataList().get(position));
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), ExploreListActivity.class.getName());

    }

    @Override
    public void onPopUpItemSelected(int selectedPosition) {
            mExploreListPresenter.onSortData(selectedPosition, exploreAdapter.getExploreDataList());
        if(selectedPosition == ExploreListActivity.POP_UP_MENU_ITEM_DISTANCE )
            filterItems.setText(getString(R.string.distance));
        else if(selectedPosition == ExploreListActivity.POP_UP_MENU_ITEM_CATEGORY)
            filterItems.setText(getString(R.string.category));
        else
            filterItems.setText(getString(R.string.rating));

    }

    @Override
    public void onSortedData() {
        exploreAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(exploreAdapter != null && exploreAdapter.getItemCount() > 0)
            getMenuInflater().inflate(R.menu.add_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_address) {
            isAddressVisible = !isAddressVisible;
            exploreAdapter.setAddressToVisible(isAddressVisible);
            item.setTitle( !isAddressVisible ? getString(R.string.show_address) : getString(R.string.hide_address));

            return true;
        }else if(id == R.id.action_contact){
            isContactVisible = !isContactVisible;
            exploreAdapter.setContactToVisible(isContactVisible);
            item.setTitle(!isContactVisible ? getString(R.string.show_contact) : getString(R.string.hide_contact));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mExploreListPresenter.getLastLocation(this, mFusedLocationClient);
            } else {
                mExploreListPresenter.showSnackbar(this, R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


}
