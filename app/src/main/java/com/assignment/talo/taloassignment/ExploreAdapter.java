package com.assignment.talo.taloassignment;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assignment.talo.taloassignment.utils.Utils;

import java.util.List;

/**
 * Created by vivek on 11/02/18.
 */

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.MyViewHolder> {

    private List<ExploreData> exploreDataList;
    private ItemClickListener mClickListener;
    boolean isAddressVisible = false, isContactVisible = false;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameText;
        public TextView distanceText;
        public TextView categoryText;
        public TextView addressText;
        public TextView contactText;
        public TextView ratingText;

        public MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.explore_name);
            distanceText = (TextView) view.findViewById(R.id.explore_distance);
            categoryText = (TextView) view.findViewById(R.id.explore_category);
            addressText = (TextView) view.findViewById(R.id.explore_address);
            contactText = (TextView) view.findViewById(R.id.explore_contact);
            ratingText = (TextView) view.findViewById(R.id.rating_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    public ExploreAdapter(List<ExploreData> exploreData) {
        this.exploreDataList = exploreData;
    }

    public List<ExploreData> getExploreDataList(){
        return exploreDataList;
    }

    public void setAddressToVisible(boolean isVisible){
        isAddressVisible = isVisible;
        notifyDataSetChanged();
    }

    public void setContactToVisible(boolean isVisible){
        isContactVisible = isVisible;
        notifyDataSetChanged();
    }

    @Override
    public ExploreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_grid_item, parent, false);

        return new ExploreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExploreAdapter.MyViewHolder holder, final int position) {
        final ExploreData exploreData = exploreDataList.get(position);
        holder.nameText.setText(exploreData.getName());
        holder.distanceText.setText("Around "+exploreData.getDistance()+" mtrs");
        holder.categoryText.setText(exploreData.getCategory());
        holder.addressText.setText(exploreData.getAddress());
        holder.contactText.setText(exploreData.getContactNo()!= null ? exploreData.getContactNo() : holder.contactText.getContext().getString(R.string.no_contact));
        holder.addressText.setVisibility( isAddressVisible ? View.VISIBLE : View.GONE);
        holder.contactText.setVisibility(isContactVisible ? View.VISIBLE : View.GONE);
        setRatingTextColor(holder.ratingText, exploreData.getRating());

    }

    /**
     * Function to determine the logic of the color for the circle
     * @param ratingText is the textview in which circle is drawn
     * @param rating is the number to be displayed inside the circle
     */
    private void setRatingTextColor(TextView ratingText, Float rating){

        int color = ContextCompat.getColor(ratingText.getContext(), R.color.green);

        if(rating < 8)
            color = rating > 5 && rating < 8 ? ContextCompat.getColor(ratingText.getContext(), R.color.yellow) :
                    ContextCompat.getColor(ratingText.getContext(), R.color.red);

        ratingText.setBackground(Utils.drawCircle (50, 50, color));
        ratingText.setText (rating+"");

    }

    @Override
    public int getItemCount() {
        return exploreDataList.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}