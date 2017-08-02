package com.epam.androidlab.museum.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.db.MuseumMapper;
import com.epam.androidlab.museum.db.MuseumRealm;
import com.epam.androidlab.museum.model.Museum;
import com.epam.androidlab.museum.model.MuseumDetailResponse;
import com.epam.androidlab.museum.network.FoursquareApiService;
import com.epam.androidlab.museum.network.FoursquareServiceHelper;
import com.epam.androidlab.museum.ui.BaseActivity;
import com.epam.androidlab.museum.ui.gallery.PhotosGalleryActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_API_VERSION;
import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_CLIENT_ID;
import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_CLIENT_SECRET;

public class MuseumDetailActivity extends BaseActivity implements PhotosAdapter.OnPhotoClickListener {

    public static final String EXTRA_KEY_MUSEUM = "museum";
    public static final String DIRECTIONS_URL = "http://maps.google.com/maps?daddr=%f,%f";
    public static final String PHONE_SCHEME = "tel:%s";
    public static final String NEW_LINE = "\n";
    public static final String ID = "id";

    private Museum museum;


    @BindView(R.id.root)
    View root;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.tv_directions)
    TextView tvDirections;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_site)
    TextView tvSite;
    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    @BindView(R.id.cb_favourite)
    CheckBox cbFavourite;

    private PhotosAdapter photosAdapter;
    private Realm realm;
    private MuseumRealm museumRealm;
    private String museumId;


    public static void start(Context context, Museum museum) {
        Intent intent = new Intent(context, MuseumDetailActivity.class);
        intent.putExtra(EXTRA_KEY_MUSEUM, museum);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cbFavourite.setVisibility(View.VISIBLE);
        cbFavourite.setChecked(false);

        museum = ((Museum) getIntent().getExtras().getSerializable(EXTRA_KEY_MUSEUM));
        fillInData(museum);
        setTitle(museum.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMuseum();
            }
        });

        museumId = museum.getId();
        museumRealm = realm.where(MuseumRealm.class)
                .equalTo(ID, museumId)
                .findFirst();

        cbFavourite.setChecked(museumRealm != null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(rvPhotos.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        photosAdapter = new PhotosAdapter(this);
        rvPhotos.setAdapter(photosAdapter);

        getMuseum();
    }

    private void fillInData(Museum museum) {
        this.museum = museum;
        if (museum.getRating() != null) {
            tvRating.setVisibility(View.VISIBLE);
            tvRating.setText(getString(R.string.text_rating, museum.getRating().toString(), museum.getRatingSignals().toString()));
        } else {
            tvRating.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(museum.getLocation().getAddress())) {
            tvDirections.setText(museum.getName());
        } else {
            tvDirections.setText(museum.getName() + NEW_LINE + museum.getLocation().getAddress());
        }
        if (museum.getContact() != null && !TextUtils.isEmpty(museum.getContact().getFormattedPhone())) {
            tvPhone.setText(museum.getContact().getFormattedPhone());
            tvPhone.setVisibility(View.VISIBLE);
        } else {
            tvPhone.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(museum.getDescription())) {
            tvDescription.setText(museum.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(museum.getShortUrl()) || !TextUtils.isEmpty(museum.getUrl())) {
            tvSite.setText(TextUtils.isEmpty(museum.getUrl()) ? museum.getShortUrl() : museum.getUrl());
            tvSite.setVisibility(View.VISIBLE);
        } else {
            tvSite.setVisibility(View.GONE);
        }
        if (museum.getPhotos() != null && museum.getPhotos().getGroups() != null && museum.getPhotos().getGroups().size() > 0) {
            photosAdapter.setItems(museum.getPhotos().getGroups().get(0).getPhotos());
        }
    }

    @OnClick(R.id.tv_directions)
    void onDirectionsClicked() {
        String uri = String.format(Locale.ENGLISH, DIRECTIONS_URL, museum.getLocation().getLat(), museum.getLocation().getLng());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @OnClick(R.id.tv_phone)
    void onPhoneClicked() {
        String uri = String.format(Locale.ENGLISH, PHONE_SCHEME, museum.getContact().getFormattedPhone());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getMuseum() {
        swipeRefreshLayout.setRefreshing(true);
        FoursquareApiService api = FoursquareServiceHelper.getApiService();
        Call<MuseumDetailResponse> call = api.getMuseumDetail(museum.getId(), FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, FOURSQUARE_API_VERSION);
        call.enqueue(new Callback<MuseumDetailResponse>() {
            @Override
            public void onResponse(Call<MuseumDetailResponse> call, Response<MuseumDetailResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    fillInData(response.body().getResponse().getMuseum());

                } else {
                    showDefaultError(root);
                }
            }

            @Override
            public void onFailure(Call<MuseumDetailResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showDefaultError(root);
            }
        });
    }

    @Override
    public void onPhotoClick(int position) {
        PhotosGalleryActivity.start(this, museum.getPhotos().getGroups().get(0).getPhotos(), position);
    }

    public void saveMuseumToDatabase() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                museumRealm = MuseumMapper.map(museum);
                realm.copyToRealmOrUpdate(museumRealm);
            }
        });
    }

    @OnCheckedChanged(R.id.cb_favourite)
    public void setFavourite() {
        if (cbFavourite.isChecked()) {
            saveMuseumToDatabase();
        } else {
            deleteMuseumFromDatabase();
        }
    }

    public void deleteMuseumFromDatabase() {
        museumRealm = realm.where(MuseumRealm.class)
                .equalTo(ID, museumId)
                .findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                museumRealm.deleteFromRealm();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

