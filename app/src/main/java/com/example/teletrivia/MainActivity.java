package com.example.teletrivia;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner categoriaSpinner, dificultadSpinner;
    private EditText cantidadEditText;
    private Button btnComprobarConexion, btnComenzar;
    private boolean conexionOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoriaSpinner = findViewById(R.id.spinnerCategoria);
        dificultadSpinner = findViewById(R.id.spinnerDificultad);
        cantidadEditText = findViewById(R.id.editCantidad);
        btnComprobarConexion = findViewById(R.id.btnConexion);
        btnComenzar = findViewById(R.id.btnComenzar);

        btnComenzar.setEnabled(false);

        String[] categorias = {"Cultura General", "Libros", "Películas", "Música", "Computación", "Matemática", "Deportes", "Historia"};
        String[] dificultades = {"Fácil", "Medio", "Difícil"};

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaSpinner.setAdapter(adapterCat);

        ArrayAdapter<String> adapterDif = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dificultades);
        dificultadSpinner.setAdapter(adapterDif);

        btnComprobarConexion.setOnClickListener(v -> {
            if (isConnected()) {
                conexionOk = true;
                Toast.makeText(MainActivity.this, "Conexión a Internet Exitosa", Toast.LENGTH_SHORT).show();
                btnComenzar.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });

        btnComenzar.setOnClickListener(v -> {
            String categoria = categoriaSpinner.getSelectedItem().toString();
            String dificultad = dificultadSpinner.getSelectedItem().toString();
            String cantidadStr = cantidadEditText.getText().toString();

            if (cantidadStr.isEmpty() || Integer.parseInt(cantidadStr) <= 0) {
                Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent intent = new Intent(this, TriviaActivity.class); // aún por crear
            intent.putExtra("categoria", categoria);
            intent.putExtra("dificultad", dificultad);
            intent.putExtra("cantidad", cantidadStr);
            startActivity(intent);
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
