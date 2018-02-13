package com.assignment.talo.taloassignment.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.assignment.talo.taloassignment.ExploreData;
import com.assignment.talo.taloassignment.contract.ExploreListContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivek on 10/02/18.
 */

public class ExploreListFetcher {

    /**
     * Function to make the API call and return the result
     * @param mContext is the activity context
     * @param location is the storeID of the restaurant
     */
    public void getExploreList(Context mContext, String location, final ExploreListContract.IFetchExploreList iFetchExploreList){

        RequestQueue queue = NetworkHelper.getRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(NetworkHelper.SERVER_URL + NetworkHelper.SERVER_GET_EXPLORE_LIST + location,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        iFetchExploreList.onSuccess(populateExploreData(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iFetchExploreList.onFailure(error.getMessage());
            }
        });
        queue.add(stringRequest);

    }

    /**
     * Function that parse the response and stores in the local modal
     * @param response is the response string
     * @return the local data modal RestaurantData object
     */
    private List<ExploreData> populateExploreData(String response) {

        List<ExploreData> exploreDataList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray groupArray = responseObject.getJSONArray("groups");
            JSONArray itemsArray = groupArray.getJSONObject(0).getJSONArray("items");

            for(int i=0; i<itemsArray.length();i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);
                JSONObject venueObject = itemObject.getJSONObject("venue");
                JSONObject locationObject = venueObject.getJSONObject("location");

                JSONArray categories = venueObject.getJSONArray("categories");

                ExploreData exploreData = new ExploreData();
                exploreData.setName(venueObject.getString("name"));
                exploreData.setDistance(Integer.parseInt(locationObject.getString("distance")));
                exploreData.setAddress(locationObject.getJSONArray("formattedAddress").toString());
                exploreData.setLocation(locationObject.getString("lat")+","+locationObject.getString("lng"));
                exploreData.setCategory(categories.getJSONObject(0).getString("shortName"));
                if(venueObject.has("rating"))
                    exploreData.setRating(Float.parseFloat(venueObject.getString("rating")));
                if(venueObject.has("hours") && venueObject.getJSONObject("hours").has("status"))
                    exploreData.setHoursOpen(venueObject.getJSONObject("hours").getString("status"));
                if(venueObject.has("price"))
                    exploreData.setPrice(venueObject.getJSONObject("price").getString("message")+"#"+
                                        venueObject.getJSONObject("price").getString("currency"));
                if(venueObject.has("url"))
                    exploreData.setUrl(venueObject.getString("url"));

                if(venueObject.has("contact") && venueObject.getJSONObject("contact").has("phone"))
                    exploreData.setContactNo(venueObject.getJSONObject("contact").getString("phone"));

                exploreDataList.add(exploreData);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return exploreDataList;
    }
}
