package com.epam.androidlab.museum.ui.gallery;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.model.Photo;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


class PhotosGalleryAdapter extends RecyclerView.Adapter<PhotosGalleryAdapter.PhotoVH> {

    private static final String PHOTO_SIZE = "700x900";
    private static final String DATE_FORMAT = "dd MMM yyyy";

    private List<Photo> items = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_gallery, parent, false);
        return new PhotoVH(view);
    }

    @Override
    public void onBindViewHolder(PhotoVH holder, int position) {
        final Photo item = getItem(position);
        String imgUrl = item.getPrefix() + PHOTO_SIZE + item.getSuffix();
        Picasso.with(holder.ivPhoto.getContext()).load(imgUrl).into(holder.ivPhoto);
        holder.tvUserName.setText(item.getUser().getFirstName() + " " + (TextUtils.isEmpty(item.getUser().getLastName()) ? "" : item.getUser().getLastName()));
        String postedAt = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(item.getCreatedAt() * DateUtils.SECOND_IN_MILLIS));
        holder.tvPostedAt.setText(holder.tvPostedAt.getContext().getString(R.string.text_posted_at, postedAt));
    }

    private Photo getItem(int position) {
        return items.get(position);
    }

    static class PhotoVH extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_posted_at)
        TextView tvPostedAt;

        PhotoVH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
