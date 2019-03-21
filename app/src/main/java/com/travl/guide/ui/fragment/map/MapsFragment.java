package com.travl.guide.ui.fragment.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.travl.guide.R;
import com.travl.guide.mvp.model.user.User;
import com.travl.guide.mvp.presenter.MapsPresenter;
import com.travl.guide.mvp.view.MapsView;
import com.travl.guide.ui.App;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapsFragment extends MvpAppCompatFragment implements MapsView, PermissionsListener {

    @InjectPresenter
    MapsPresenter presenter;
    @BindView(R.id.mapView)
    MapView mapView;

    private LocationComponent locationComponent;
    private MapboxMap mapBoxMap;
    private PermissionsManager permissionsManager;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final String PLACES_GEO_SOURCE = "places_geo_source";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        FloatingActionButton floatingActionButton = activity.findViewById(R.id.app_bar_fab);
        if(floatingActionButton != null) {
            floatingActionButton.setOnClickListener(view -> fabClick());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        App.getInstance().getAppComponent().inject(this);
        mapView.onCreate(savedInstanceState);
        setupViews();
        return view;
    }

    private void setupViews() {
        presenter.setupMapView();
    }

    public void fabClick() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                .build(getActivity());
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public void setupMapBox() {
        mapView.getMapAsync(mapBoxMap -> {
            this.mapBoxMap = mapBoxMap;
            mapBoxMap.setStyle(new Style.Builder().fromUrl(getString(R.string.mapbox_syle_link_minimo)), style -> {
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapBoxMap, style);
                localizationPlugin.matchMapLanguageWithDeviceDefault();
                presenter.loadPlacesForMap();
                presenter.showLocations();
            });
        });
    }

    @Override
    public void onPlacesLoaded(List<Feature> markerCoordinates) {
        Style style = mapBoxMap.getStyle();
        if(style != null) {
            GeoJsonSource geoJsonSource = new GeoJsonSource(PLACES_GEO_SOURCE, FeatureCollection.fromFeatures(markerCoordinates));
            style.addSource(geoJsonSource);
            style.addImage("place_image", getResources().getDrawable(R.drawable.ic_place_black));
            style.addLayer(new SymbolLayer("marker-layer", PLACES_GEO_SOURCE)
                    .withProperties(PropertyFactory.iconImage("place_image"),
                            iconOffset(new Float[] {0f, - 9f})));
        }

    }

    @Override
    @SuppressLint("MissingPermission")
    public void findUser() {
        if(PermissionsManager.areLocationPermissionsGranted(App.getInstance())) {
            locationComponent = mapBoxMap.getLocationComponent();
            locationComponent.activateLocationComponent(App.getInstance(), Objects.requireNonNull(mapBoxMap.getStyle()));
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else if(! PermissionsManager.areLocationPermissionsGranted(App.getInstance())) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
        setUserCoordinates();
    }

    @SuppressLint("MissingPermission")
    private void setUserCoordinates() {
        double[] coordinates = new double[2];
        coordinates[0] = 0;
        coordinates[1] = 0;
        if (locationComponent != null) {
            Location location = locationComponent.getLastKnownLocation();
            if (location != null) {
                coordinates[0] = location.getLatitude();
                coordinates[1] = location.getLongitude();
                Timber.e("User coordinates set to: " + coordinates[0] + ", " + coordinates[1]);
            }
        }
        User.getInstance().setCoordinates(coordinates);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("Result query with find place");
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            if(mapBoxMap != null) {
                Style style = mapBoxMap.getStyle();
                if(style != null) {
                    String geoJsonSourceLayerId = "geoJsonSourceLayerId";
                    GeoJsonSource source = style.getSourceAs(geoJsonSourceLayerId);
                    if(source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                                            ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), "This app needs location permissions in order to show its functionality.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted) findUser();
        else
            Toast.makeText(getContext(), "You didn\'t grant location permissions.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @ProvidePresenter
    public MapsPresenter providePresenter() {
        MapsPresenter presenter = new MapsPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}