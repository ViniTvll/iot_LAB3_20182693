package com.example.teletrivia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView textCorrectas, textIncorrectas, textNoRespondidas;
    private Button btnVolverMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        textCorrectas = findViewById(R.id.textCorrectas);
        textIncorrectas = findViewById(R.id.textIncorrectas);
        textNoRespondidas = findViewById(R.id.textNoRespondidas);
        btnVolverMenu = findViewById(R.id.btnVolverMenu);

        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int noRespondidas = getIntent().getIntExtra("no_respondidas", 0);

        textCorrectas.setText("Correctas: " + correctas);
        textIncorrectas.setText("Incorrectas: " + incorrectas);
        textNoRespondidas.setText("No respondidas: " + noRespondidas);

        btnVolverMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
