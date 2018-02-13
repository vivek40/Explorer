package com.assignment.talo.taloassignment.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.talo.taloassignment.ExploreData;
import com.assignment.talo.taloassignment.R;
import com.assignment.talo.taloassignment.utils.Constants;

/**
 * Created by vivek on 12/02/18.
 */

public class ExploreDetailDialog extends DialogFragment {

    TextView nameText, addressText, contactText, navigateText, urlText, ratingText, categoryText;
    TextView openHrsText, priceText;
    ImageView cancelBtn;
    AppCompatRatingBar ratingBar;

    static ExploreDetailDialog newInstance() {
        ExploreDetailDialog exploreDetailDialog = new ExploreDetailDialog();
        return exploreDetailDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_explore_details, container, false);
        setupViews(v);
        return v;
    }

    /**
     * Function to initialize the view from the xml
     *
     * @param view is the current top inflated layout view
     */
    private void setupViews(View view) {
        nameText = (TextView) view.findViewById(R.id.name_text);
        addressText = (TextView) view.findViewById(R.id.address_text);
        navigateText = (TextView) view.findViewById(R.id.navigate_text);
        contactText = (TextView) view.findViewById(R.id.contact_text);
        urlText = (TextView) view.findViewById(R.id.url_text);
        ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingText = (TextView) view.findViewById(R.id.rating_text);
        categoryText = (TextView) view.findViewById(R.id.explore_category);
        openHrsText = (TextView) view.findViewById(R.id.opening_hours);
        priceText = (TextView) view.findViewById(R.id.price_details);
        cancelBtn = (ImageView) view.findViewById(R.id.cancel_action);

        populateFromIntent();
    }

    /**
     * Function to retrive and populate the selcted item value from the previous screen
     */
    private void populateFromIntent() {
        ExploreData exploreData = (ExploreData) getArguments().getParcelable(Constants.SELECTED_ITEM);
        String address = exploreData.getAddress().replaceAll(",", ",\n");
        address = address.replace("[", "");
        address = address.replace("]", "");
        address = address.replace("\"", "");
        nameText.setText(exploreData.getName());
        addressText.setText(address);
        contactText.setText(exploreData.getContactNo() != null && exploreData.getContactNo().length() > 0 ? exploreData.getContactNo() : "-");
        urlText.setText(exploreData.getUrl() != null && exploreData.getUrl().length() > 0 ? exploreData.getUrl() : getString(R.string.no_website));
        ratingBar.setRating(exploreData.getRating());
        ratingText.setText("( " + exploreData.getRating() + " )");
        categoryText.setText(exploreData.getCategory());

        openHrsText.setText(exploreData.getHoursOpen() != null ? exploreData.getHoursOpen() : getString(R.string.no_timings));

        priceText.setText(exploreData.getPrice() != null && exploreData.getPrice().length() > 0 && exploreData.getPrice().contains("#") ?
                getString(R.string.price_range) + exploreData.getPrice().split("#")[0] + " \n\n " + getString(R.string.currency_accepted) + exploreData.getPrice().split("#")[1]
                : getString(R.string.no_price));

        setClickListeners(exploreData.getLocation());
    }

    /**
     * Function that handles all click events in this class
     *
     * @param location
     */
    private void setClickListeners(final String location) {

        navigateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null && location.contains(",")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + location.split(",")[0] + ">,<" + location.split(",")[1] + ">?q=<" + location.split(",")[0] + ">,<" + location.split(",")[1] + ">(" + nameText.getText().toString() + ")"));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }


            }
        });

        contactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactText.getText().toString().length() > 1) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactText.getText().toString()));
                    startActivity(intent);
                }

            }
        });

        urlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!urlText.getText().toString().equals(getString(R.string.no_website))) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlText.getText().toString()));
                    startActivity(i);
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


}
