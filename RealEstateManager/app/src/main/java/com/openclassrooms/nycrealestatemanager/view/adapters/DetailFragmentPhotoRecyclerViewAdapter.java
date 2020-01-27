package com.openclassrooms.nycrealestatemanager.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.model.Photo;
import com.openclassrooms.nycrealestatemanager.view.holders.DetailPhotoViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that binds photos to view
 */
public class DetailFragmentPhotoRecyclerViewAdapter extends RecyclerView.Adapter<DetailPhotoViewHolder> {

    public interface OnClickPhotoListener {
        void onClickPhoto(Photo photo);
    }

    private List<Photo> photos;
    private OnClickPhotoListener callback;

    public DetailFragmentPhotoRecyclerViewAdapter(OnClickPhotoListener callback) {
        this.callback = callback;
        this.photos = new ArrayList<>();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos.clear();
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_detail_item, parent, false);

        return new DetailPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Glide.with(holder.itemView).load(photo.getUri(holder.itemView.getContext())).into(holder.photo);
        holder.title.setText(photo.getDescription());
        holder.itemView.setOnClickListener(l -> callback.onClickPhoto(photo));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}