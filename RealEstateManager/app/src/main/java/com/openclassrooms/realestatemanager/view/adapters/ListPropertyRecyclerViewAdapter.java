package com.openclassrooms.realestatemanager.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.view.holders.PropertyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ListPropertyRecyclerViewAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    public interface PropertyOnClickListener {
        void onClickListener(Property property, List<Photo> photos);
    }

    private PropertyOnClickListener callback;
    private List<Property> propertyList;
    private List<Photo> photoList;

    public ListPropertyRecyclerViewAdapter(PropertyOnClickListener callback) {
        this.callback = callback;
        this.propertyList = new ArrayList<>();
        this.photoList = new ArrayList<>();
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList.clear();
        this.propertyList = propertyList;
        notifyDataSetChanged();
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList.clear();
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        List<Photo> photos = new ArrayList<>();
        holder.propertyType.setText(property.getType());
        holder.propertyBorough.setText(property.getBorough());
        holder.propertyPrice.setText(String.valueOf(property.getPrice()));
        holder.propertyNumberRooms.setText(String.valueOf(property.getRooms()));
        holder.propertySquareMeter.setText(String.valueOf(property.getSurface()));
        holder.propertyNumberBedroom.setText(String.valueOf(property.getBedrooms()));
        holder.propertyNumberBathrooms.setText(String.valueOf(property.getBathrooms()));
        for (Photo photo : photoList) {
            if (photo.getPropertyID() == property.getId()) {
                photos.add(photo);
            }
        }
        if (!photos.isEmpty()) {
            Glide.with(holder.itemView).load(photos.get(0).getUri()).into(holder.propertyPhoto);
        }
        holder.itemView.setOnClickListener(l -> callback.onClickListener(property, photos));
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }
}
