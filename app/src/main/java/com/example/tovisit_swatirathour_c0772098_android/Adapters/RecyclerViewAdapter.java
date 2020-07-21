package com.example.tovisit_swatirathour_c0772098_android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tovisit_swatirathour_c0772098_android.MapsActivity;
import com.example.tovisit_swatirathour_c0772098_android.R;
import com.example.tovisit_swatirathour_c0772098_android.Room.Place;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Place> mPlaceList;

    public RecyclerViewAdapter(Context mContext, List<Place> mPlaceList) {
        this.mContext = mContext;
        this.mPlaceList = mPlaceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.custim_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Place place = mPlaceList.get(position);
        holder.titleTextView.setText(place.getTitle());
        if(place.isVisited())
        {
            System.out.println("green");
            holder.mainLayout.setBackgroundColor(Color.GREEN);
        }
        else
        {
            System.out.println("white");
            holder.mainLayout.setBackgroundColor(Color.WHITE);
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("place",place);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

    public Place removePlace(int position) {
        Place place = mPlaceList.remove(position);
        this.notifyItemRemoved(position);
        return place;
    }

    public void insertPlace(Place place, int position) {
        mPlaceList.add(position,place);
        this.notifyItemInserted(position);
    }

    public Place togglePlaceVisited(int position)
    {
        mPlaceList.get(position).setVisited(!mPlaceList.get(position).isVisited());
        this.notifyItemChanged(position);
        return mPlaceList.get(position);
    }

    public boolean isVisited(int position) {
        return mPlaceList.get(position).isVisited();
    }

    public Place getItem(int position) {
        return mPlaceList.get(position);
    }

    public void setPlaces(List<Place> placeList) {
        this.mPlaceList.clear();
        this.mPlaceList.addAll(placeList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public LinearLayout mainLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textview);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
