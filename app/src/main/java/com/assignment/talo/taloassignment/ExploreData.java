package com.assignment.talo.taloassignment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vivek on 10/02/18.
 */

public class ExploreData implements Parcelable{
    String name;
    int distance;
    String category;
    String address;
    String location;
    String contactNo;
    float rating;
    String hoursOpen;
    String price;
    String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getHoursOpen() {
        return hoursOpen;
    }

    public void setHoursOpen(String hoursOpen) {
        this.hoursOpen = hoursOpen;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.distance);
        dest.writeString(this.category);
        dest.writeString(this.address);
        dest.writeString(this.location);
        dest.writeString(this.contactNo);
        dest.writeFloat(this.rating);
        dest.writeString(this.hoursOpen);
        dest.writeString(this.price);
        dest.writeString(this.url);
    }

    public ExploreData() {
    }

    protected ExploreData(Parcel in) {
        this.name = in.readString();
        this.distance = in.readInt();
        this.category = in.readString();
        this.address = in.readString();
        this.location = in.readString();
        this.contactNo = in.readString();
        this.rating = in.readFloat();
        this.hoursOpen = in.readString();
        this.price = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ExploreData> CREATOR = new Creator<ExploreData>() {
        @Override
        public ExploreData createFromParcel(Parcel source) {
            return new ExploreData(source);
        }

        @Override
        public ExploreData[] newArray(int size) {
            return new ExploreData[size];
        }
    };
}
