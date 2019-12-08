package com.openclassrooms.realestatemanager.view.holders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_edit_item_photo)
    public ImageView photo;
    @BindView(R.id.activity_edit_item_title)
    public TextView title;
    @BindView(R.id.activity_edit_item_delete)
    public ImageButton deleteBtn;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
