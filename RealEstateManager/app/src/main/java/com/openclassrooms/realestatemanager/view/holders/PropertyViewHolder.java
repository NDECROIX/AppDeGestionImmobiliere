package com.openclassrooms.realestatemanager.view.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PropertyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.fragment_list_item_photo)
    public ImageView propertyPhoto;
    @BindView(R.id.fragment_list_item_type)
    public TextView propertyType;
    @BindView(R.id.fragment_list_item_borough)
    public TextView propertyBorough;
    @BindView(R.id.fragment_list_item_price)
    public TextView propertyPrice;
    @Nullable
    @BindView(R.id.fragment_list_item_home)
    public TextView propertyNumberRooms;
    @Nullable
    @BindView(R.id.fragment_list_item_surface)
    public TextView propertySquareMeter;
    @Nullable
    @BindView(R.id.fragment_list_item_bathroom)
    public TextView propertyNumberBathrooms;
    @Nullable
    @BindView(R.id.fragment_list_item_bedroom)
    public TextView propertyNumberBedroom;

    public PropertyViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}