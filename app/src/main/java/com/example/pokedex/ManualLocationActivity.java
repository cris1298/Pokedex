package com.example.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManualLocationActivity extends AppCompatActivity {

    private EditText editLat, editLng;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_location);

        editLat = findViewById(R.id.editLat);
        editLng = findViewById(R.id.editLng);
        btnCreate = findViewById(R.id.btnCreateLocation);

        btnCreate.setOnClickListener(v -> {
            try {
                double lat = Double.parseDouble(editLat.getText().toString());
                double lng = Double.parseDouble(editLng.getText().toString());

                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", lat);
                resultIntent.putExtra("longitude", lng);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Latitud o longitud inválida", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
