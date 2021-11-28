package com.example.rouminder.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.rouminder.BuildConfig;
import com.example.rouminder.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsFragment extends Fragment {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    static final String apiKey = BuildConfig.API_KEY;

    LatLng selectedLatLng = null;

    MapsFragment self = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_maps, container, false);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

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
                LatLng latLng = place.getLatLng();
                self.selectedLatLng = latLng;

                String address = place.getAddress();

                TextView addressTextView = (TextView) getActivity().findViewById(R.id.selected_place_name);
                addressTextView.setText(address);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        return layout;
    }

    public LatLng getSelectedLatLng() {
        return selectedLatLng;
    }
}