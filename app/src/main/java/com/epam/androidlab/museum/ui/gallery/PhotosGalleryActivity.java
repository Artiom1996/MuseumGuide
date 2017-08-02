package com.epam.androidlab.museum.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.model.Photo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosGalleryActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_PHOTOS = "photos";

    public static final String EXTRA_KEY_POSITION = "position";

    @BindView(R.id.root)
    View root;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private PhotosGalleryAdapter photosGalleryAdapter;

    public static void start(Context context, ArrayList<Photo> photos, int position) {
        Intent intent = new Intent(context, PhotosGalleryActivity.class);
        intent.putExtra(EXTRA_KEY_PHOTOS, photos);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_gallery);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ArrayList<Photo> photos = ((ArrayList<Photo>) getIntent().getExtras().getSerializable(EXTRA_KEY_PHOTOS));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_photos, photos.size()+""));

        setUpRecyclerView(photos);
    }

    private void setUpRecyclerView(ArrayList<Photo> photos) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rvPhotos.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        photosGalleryAdapter = new PhotosGalleryAdapter();
        rvPhotos.setAdapter(photosGalleryAdapter);
        photosGalleryAdapter.setItems(photos);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvPhotos);
        layoutManager.scrollToPosition(getIntent().getExtras().getInt(EXTRA_KEY_POSITION));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
