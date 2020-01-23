package com.openclassrooms.realestatemanager.view.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPhotoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.fragment_detail_item_photo)
    public ImageView photo;
    @BindView(R.id.fragment_detail_item_title)
    public TextView title;

    public DetailPhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}