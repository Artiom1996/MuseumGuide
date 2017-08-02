package com.epam.androidlab.museum.ui.favourite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.db.MuseumRealm;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by Artiom on 12.07.2017.
 */

class FavouriteMuseumsAdapter extends RecyclerView.Adapter<FavouriteMuseumsAdapter.FavouriteMuseumVH> {


    private RealmResults<MuseumRealm> items;

    FavouriteMuseumsAdapter(RealmResults<MuseumRealm> items, OnFavoriteClickListener onFavoriteClickListener) {
        this.items = items;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    private OnFavoriteClickListener onFavoriteClickListener;

    interface OnFavoriteClickListener {

        void onFavoriteClick(MuseumRealm museum);
    }

    void setItems(RealmResults<MuseumRealm> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public FavouriteMuseumVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite_museum, parent, false);
        return new FavouriteMuseumVH(view);
    }

    @Override
    public void onBindViewHolder(FavouriteMuseumVH holder, final int position) {
        final MuseumRealm item = getItem(position);
        holder.tvMuseumName.setText(item.getName());
        if (item.getRating() != null) {
            holder.tvMuseumRating.setText(item.getRating().toString());
            holder.tvMuseumRating.setVisibility(View.VISIBLE);
        } else {
            holder.tvMuseumRating.setVisibility(View.GONE);
        }
        holder.tvMuseumAddress.setText(item.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteClickListener.onFavoriteClick(item);
            }
        });
    }

    private MuseumRealm getItem(int position) {
        return items.get(position);
    }

    static class FavouriteMuseumVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_museum_name)
        TextView tvMuseumName;
        @BindView(R.id.tv_museum_address)
        TextView tvMuseumAddress;
        @BindView(R.id.tv_museum_rating)
        TextView tvMuseumRating;

        FavouriteMuseumVH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}