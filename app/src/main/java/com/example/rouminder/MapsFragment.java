package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    static final String apiKey = BuildConfig.API_KEY;

    MapView mapView;
    GoogleMap googleMap;

    Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_maps, container, false);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        mapView = layout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());

        // Initialize the AutocompleteSupportFragment.
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        AutocompleteSupportFragment autocompleteFragment = new AutocompleteSupportFragment();
        ft.replace(R.id.autocomplete_fragment_content, autocompleteFragment);
        ft.commit();

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                LatLng latLng = place.getLatLng();

                // remove prev marker
                marker.remove();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                markerOptions.snippet(place.getAddress());

                marker = googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                Log.i("TAG", "finish");
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        return layout;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");

        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(SEOUL);
        builder.zoom(10);
        CameraPosition position = builder.build();

        marker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        Log.i("tag", "onMapReady");

        this.googleMap = googleMap;
    }
}