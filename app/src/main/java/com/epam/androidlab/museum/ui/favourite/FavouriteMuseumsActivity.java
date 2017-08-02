package com.epam.androidlab.museum.ui.favourite;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.db.MuseumMapper;
import com.epam.androidlab.museum.db.MuseumRealm;
import com.epam.androidlab.museum.ui.BaseActivity;
import com.epam.androidlab.museum.ui.detail.MuseumDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;




/**
 * Created by Artiom on 12.07.2017.
 */

public class FavouriteMuseumsActivity extends BaseActivity implements FavouriteMuseumsAdapter.OnFavoriteClickListener {

    private  RealmResults<MuseumRealm> favouriteMuseums;
    private Realm realm;
    private FavouriteMuseumsAdapter favouriteMuseumsAdapter;

    @BindView(R.id.root)
    View root;
    @BindView(R.id.rv_favourite_museums)
    RecyclerView rvFavouriteMuseums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_museums);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_favourite_museums);

        realm = Realm.getDefaultInstance();
        favouriteMuseums = realm.where(MuseumRealm.class).findAll();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favouriteMuseumsAdapter = new FavouriteMuseumsAdapter(favouriteMuseums, this);
        rvFavouriteMuseums.setAdapter(favouriteMuseumsAdapter);
        favouriteMuseumsAdapter.setItems(favouriteMuseums);

        LinearLayoutManager layoutManager = new LinearLayoutManager(rvFavouriteMuseums.getContext(), LinearLayoutManager.VERTICAL, false);
        rvFavouriteMuseums.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        favouriteMuseums = realm.where(MuseumRealm.class).findAll();
        favouriteMuseumsAdapter.setItems(favouriteMuseums);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onFavoriteClick(MuseumRealm museumRealm) {
        MuseumDetailActivity.start(this, MuseumMapper.map(museumRealm));
    }
}