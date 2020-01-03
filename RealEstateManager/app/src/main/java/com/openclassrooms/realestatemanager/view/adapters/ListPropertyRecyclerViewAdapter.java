package com.openclassrooms.realestatemanager.view.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.holders.PropertyViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that binds properties to view
 */
public class ListPropertyRecyclerViewAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    public interface PropertyOnClickListener {
        void onClickPropertyListener(Property property, List<Photo> photos);

        void firstPropertyAdded(Property property, List<Photo> photos);

        void recyclerViewEmpty();
    }

    private PropertyOnClickListener callback;
    private List<Property> properties;
    private List<Property> propertiesBackUp;
    private List<Photo> photoList;
    private List<PoiNextProperty> poisNextProperty;
    private Filter filter;

    public ListPropertyRecyclerViewAdapter(PropertyOnClickListener callback) {
        this.callback = callback;
        this.properties = new ArrayList<>();
        this.propertiesBackUp = new ArrayList<>();
        this.photoList = new ArrayList<>();
        this.poisNextProperty = new ArrayList<>();
    }

    public void setProperties(List<Property> properties) {
        this.properties.clear();
        this.propertiesBackUp.clear();
        this.properties.addAll(properties);
        this.propertiesBackUp.addAll(properties);
        notifyDataSetChanged();
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList.clear();
        this.photoList.addAll(photoList);
        notifyDataSetChanged();
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        filterProperty();
        notifyDataSetChanged();
    }

    public void setPoisNextProperty(List<PoiNextProperty> poisNextProperty) {
        this.poisNextProperty.clear();
        this.poisNextProperty.addAll(poisNextProperty);
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
        Property property = properties.get(position);
        List<Photo> photos = new ArrayList<>();
        holder.propertyType.setText(property.getType());
        holder.propertyBorough.setText(property.getBorough());
        holder.propertyPrice.setText(Utils.getPrice(property.getPrice()));
        if (holder.propertyNumberRooms != null && holder.propertySquareMeter != null
                && holder.propertyNumberBedroom != null && holder.propertyNumberBathrooms != null) {
            holder.propertyNumberRooms.setText(String.valueOf(property.getRooms()));
            holder.propertySquareMeter.setText(String.valueOf(property.getSurface()));
            holder.propertyNumberBedroom.setText(String.valueOf(property.getBedrooms()));
            holder.propertyNumberBathrooms.setText(String.valueOf(property.getBathrooms()));
        }
        for (Photo photo : photoList) {
            if (photo.getPropertyID().equals(property.getId())) {
                photos.add(photo);
            }
        }
        if (!photos.isEmpty()) {
            Glide.with(holder.itemView).load(photos.get(0).getUri(holder.itemView.getContext())).into(holder.propertyPhoto);
        }
        holder.itemView.setOnClickListener(l -> callback.onClickPropertyListener(property, photos));
        if (position == 0) callback.firstPropertyAdded(property, photos);
        holder.itemView.setFocusableInTouchMode(true);
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.callOnClick();
                v.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryDark));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    /**
     * Filter the properties
     */
    private void filterProperty() {
        if (filter == null) {
            return;
        }
        List<Property> cloneProperties = new ArrayList<>(propertiesBackUp);
        properties.clear();
        properties.addAll(propertiesBackUp);
        for (Property property : cloneProperties) {
            int photos = 0;
            List<Poi> pois = new ArrayList<>();
            for (Photo photo : photoList) {
                if (photo.getPropertyID().equals(property.getId())) {
                    photos++;
                }
            }
            for (PoiNextProperty poiNextProperty : poisNextProperty) {
                if (poiNextProperty.getPropertyID().equals(property.getId())) {
                    Poi poi = new Poi();
                    poi.setName(poiNextProperty.getPoiName());
                    pois.add(poi);
                }
            }
            if (!filter.meetsCriteria(property, photos, pois)) {
                properties.remove(property);
            }
        }
        if (getItemCount() == 0) {
            callback.recyclerViewEmpty();
        }
    }
}
