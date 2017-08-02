package com.epam.androidlab.museum.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoVH> {

    private static final String PHOTO_SIZE = "400x400";

    private List<Photo> items = new ArrayList<>();

    private OnPhotoClickListener onPhotoClickListener;

    interface OnPhotoClickListener {

        void onPhotoClick(int position);
    }

    PhotosAdapter(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    void setItems(List<Photo> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoVH(view);
    }

    @Override
    public void onBindViewHolder(PhotoVH holder, final int position) {
        final Photo item = getItem(position);
        String imgUrl = item.getPrefix() + PHOTO_SIZE + item.getSuffix();
        Picasso.with(holder.ivPhoto.getContext()).load(imgUrl).fit().into(holder.ivPhoto);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoClickListener.onPhotoClick(position);
            }
        });
    }

    private Photo getItem(int position) {
        return items.get(position);
    }

    static class PhotoVH extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        PhotoVH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
