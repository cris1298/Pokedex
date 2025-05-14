    package com.example.pokedex;

    import android.content.Context;
    import android.content.Intent;
    import android.location.Location;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.pokedex.models.PokemonDetail;
    import com.example.pokedex.models.UbiLocation;
    import com.example.pokedex.services.PokeApiService;
    import com.squareup.picasso.Picasso;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class DetailActivity extends AppCompatActivity {
        private ImageView imageView;
        private TextView nameView, typesView;
        private PokemonDetail currentPokemon;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            imageView = findViewById(R.id.imageView);
            nameView = findViewById(R.id.nameView);
            typesView = findViewById(R.id.typesView);

            String url = getIntent().getStringExtra("url");

            Button btnSave = findViewById(R.id.btnSaveLocation);
            Button btnMap = findViewById(R.id.btnViewMap);

            Retrofit locationRetrofit = new Retrofit.Builder()
                    .baseUrl("https://6823f20365ba058033985754.mockapi.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PokeApiService locationApi = locationRetrofit.create(PokeApiService.class);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://pokeapi.co/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PokeApiService api = retrofit.create(PokeApiService.class);

            api.getPokemonDetail(url).enqueue(new Callback<PokemonDetail>() {
                @Override
                public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PokemonDetail p = response.body();
                        currentPokemon = response.body(); // NECESARIO para que no sea null
                        nameView.setText(capitalize(p.getName()));
                        Picasso.get().load(p.getSprites().front_default).into(imageView);

                        StringBuilder types = new StringBuilder("Tipo: ");
                        for (PokemonDetail.TypeSlot t : p.getTypes()) {
                            types.append(capitalize(t.type.name)).append(" ");
                        }
                        typesView.setText(types.toString().trim());
                    }
                }

                @Override
                public void onFailure(Call<PokemonDetail> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error al cargar detalles", Toast.LENGTH_SHORT).show();
                }
            });

            btnSave.setOnClickListener(v -> {
                Intent intent = new Intent(DetailActivity.this, ManualLocationActivity.class);
                startActivityForResult(intent, 100);  // requestCode = 100
            });

            btnMap.setOnClickListener(v -> {
                if (currentPokemon == null) {
                    Toast.makeText(this, "Pokémon no cargado aún", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("pokemonName", capitalize(currentPokemon.getName()));
                startActivity(intent);
            });

        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
                double lat = data.getDoubleExtra("latitude", 0);
                double lng = data.getDoubleExtra("longitude", 0);

                if (currentPokemon != null) {
                    Retrofit locationRetrofit = new Retrofit.Builder()
                            .baseUrl("https://6823f20365ba058033985754.mockapi.io/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    PokeApiService locationApi = locationRetrofit.create(PokeApiService.class);

                    UbiLocation ubi = new UbiLocation();
                    ubi.pokemonName = capitalize(currentPokemon.getName());
                    ubi.latitude = lat;
                    ubi.longitude = lng;

                    locationApi.saveLocation(ubi).enqueue(new Callback<UbiLocation>() {
                        @Override
                        public void onResponse(Call<UbiLocation> call, Response<UbiLocation> response) {
                            Toast.makeText(DetailActivity.this, "Ubicación guardada manualmente", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<UbiLocation> call, Throwable t) {
                            Toast.makeText(DetailActivity.this, "Error al guardar ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        private String capitalize(String s) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }


    }