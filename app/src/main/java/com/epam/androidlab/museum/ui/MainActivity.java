package com.epam.androidlab.museum.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.epam.androidlab.museum.R;
import com.epam.androidlab.museum.model.Museum;
import com.epam.androidlab.museum.model.MuseumListResponse;
import com.epam.androidlab.museum.network.FoursquareApiService;
import com.epam.androidlab.museum.network.FoursquareServiceHelper;
import com.epam.androidlab.museum.ui.detail.MuseumDetailActivity;
import com.epam.androidlab.museum.ui.favourite.FavouriteMuseumsActivity;
import com.facebook.stetho.Stetho;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_API_VERSION;
import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_CLIENT_ID;
import static com.epam.androidlab.museum.network.FoursquareServiceHelper.FOURSQUARE_CLIENT_SECRET;
import static com.epam.androidlab.museum.network.FoursquareServiceHelper.MUSEUM_CATEGORY_ID;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    public static final float DEFAULT_ZOOM = 11.0f;
    public static final String RADIUS = String.valueOf(100000);
    private GoogleMap map;


    @BindView(R.id.root)
    View root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sv_find_museum)
    SearchView svFindMuseum;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        svFindMuseum.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                
                if (!svFindMuseum.getQuery().toString().isEmpty() && map != null) {

                    LatLng center = map.getCameraPosition().target;
                    Location location = new Location("");
                    location.setLatitude(center.latitude);
                    location.setLongitude(center.longitude);

                    String queue = svFindMuseum.getQuery().toString();
                    getMuseumsByQueue(location, queue);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });


    }


    @OnClick(R.id.btn_search_this_area)
    void onSearchThisAreaClicked() {
        LatLng center = map.getCameraPosition().target;
        Location location = new Location("");
        location.setLatitude(center.latitude);
        location.setLongitude(center.longitude);
        getMuseums(location);
    }


    private void getMuseums(Location location) {
        FoursquareApiService api = FoursquareServiceHelper.getApiService();

        Call<MuseumListResponse> call = api.getMuseumList(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, FOURSQUARE_API_VERSION, location.getLatitude() + "," + location.getLongitude(), MUSEUM_CATEGORY_ID, null , null);
        call.enqueue(new Callback<MuseumListResponse>() {
            @Override
            public void onResponse(Call<MuseumListResponse> call, Response<MuseumListResponse> response) {
                if (response.isSuccessful()) {
                    map.clear();
                    ArrayList<Museum> museums = response.body().getResponse().getMuseums();
                    if (museums.isEmpty()) {
                        showError(R.string.error_no_museums_found, root);
                        return;
                    }
                    showMuseumsOnMap(museums);
                } else {
                    showDefaultError(root);
                }
            }

            @Override
            public void onFailure(Call<MuseumListResponse> call, Throwable t) {
                showDefaultError(root);
            }
        });
    }

    private void showMuseumsOnMap(ArrayList<Museum> museums) {
        for (Museum museum : museums) {
            LatLng coordinates = new LatLng(museum.getLocation().getLat(), museum.getLocation().getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(coordinates)
                    .title(museum.getName());
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(museum);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //set default zoom level
        CameraUpdate cameraUpdateZoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);
        map.moveCamera(cameraUpdateZoom);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MuseumDetailActivity.start(MainActivity.this, (Museum) marker.getTag());
            }
        });


        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            onLocationRetrieved(location);
                        } else {
                            showError(R.string.error_location_not_found, root);
                        }
                    }
                });
    }

    @SuppressWarnings("MissingPermission")
    private void onLocationRetrieved(Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        map.animateCamera(cameraUpdate);

        getMuseums(location);

        map.setMyLocationEnabled(true);
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) | ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                showError(R.string.error_location_not_found, root);

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                showError(R.string.error_location_not_found, root);
            }
        }
    }

    @OnClick(R.id.btn_show_favourite_museums)
    public void showFavouriteMuseums() {
        Intent intent = new Intent(this, FavouriteMuseumsActivity.class);
        startActivity(intent);
    }

    public void getMuseumsByQueue(Location location, String queue) {
        FoursquareApiService api = FoursquareServiceHelper.getApiService();

        Call<MuseumListResponse> call = api.getMuseumList(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, FOURSQUARE_API_VERSION, location.getLatitude() + "," + location.getLongitude(), MUSEUM_CATEGORY_ID, queue, RADIUS);
        call.enqueue(new Callback<MuseumListResponse>() {
            @Override
            public void onResponse(Call<MuseumListResponse> call, Response<MuseumListResponse> response) {
                if (response.isSuccessful()) {
                    map.clear();
                    ArrayList<Museum> museums = response.body().getResponse().getMuseums();
                    if (museums.isEmpty()) {
                        showError(R.string.error_no_museums_found, root);
                        return;
                    }
                    showMuseumsOnMap(museums);
                } else {
                    showDefaultError(root);
                }
            }

            @Override
            public void onFailure(Call<MuseumListResponse> call, Throwable t) {
                showDefaultError(root);
            }
        });
    }


}
