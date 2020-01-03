package com.openclassrooms.realestatemanager.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.view.holders.PhotoViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that binds photos to view
 */
public class EditActivityPhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    public interface OnClickPhotoListener {
        void onClickDeletePhoto(Photo photo);
    }

    private OnClickPhotoListener callback;
    private List<Photo> photos;

    public EditActivityPhotoRecyclerViewAdapter(OnClickPhotoListener callback) {
        this.callback = callback;
        this.photos = new ArrayList<>();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos.clear();
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    public void setPhoto(Photo photos) {
        this.photos.add(photos);
        notifyDataSetChanged();
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_edit_item, parent, false);

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Glide.with(holder.itemView).load(photo.getUri()).into(holder.photo);
        holder.title.setText(photo.getDescription());
        holder.deleteBtn.setOnClickListener(l -> {
            callback.onClickDeletePhoto(photo);
            photos.remove(photo);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}