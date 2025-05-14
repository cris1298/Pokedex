package com.example.pokedex;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.pokedex.models.UbiLocation;
import com.example.pokedex.services.PokeApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pokedex.databinding.ActivityMapsBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String name = getIntent().getStringExtra("pokemonName");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6823f20365ba058033985754.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PokeApiService api = retrofit.create(PokeApiService.class);

        api.getLocationsByPokemon(name).enqueue(new Callback<List<UbiLocation>>() {
            @Override
            public void onResponse(Call<List<UbiLocation>> call, Response<List<UbiLocation>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                    for (UbiLocation loc : response.body()) {
                        LatLng point = new LatLng(loc.latitude, loc.longitude);
                        mMap.addMarker(new MarkerOptions().position(point).title(loc.pokemonName));
                        boundsBuilder.include(point);  // Agregar al límite
                    }

                    // Ajustar la cámara para mostrar todos los puntos
                    mMap.setOnMapLoadedCallback(() -> {
                        LatLngBounds bounds = boundsBuilder.build();
                        int padding = 100; // espacio alrededor de los bordes del mapa
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    });
                }
            }


            @Override
            public void onFailure(Call<List<UbiLocation>> call, Throwable t) {
                // Error
            }
        });
    }
}