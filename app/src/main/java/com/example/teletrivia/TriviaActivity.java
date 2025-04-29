package com.example.teletrivia;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teletrivia.api.TriviaApiService;
import com.example.teletrivia.model.Pregunta;
import com.example.teletrivia.model.TriviaResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TriviaActivity extends AppCompatActivity {

    private TextView textPregunta, textContador;
    private RadioGroup grupoOpciones;
    private Button btnSiguiente;

    private List<Pregunta> preguntas = new ArrayList<>();
    private int indexActual = 0;

    private int respuestasCorrectas = 0;
    private int respuestasIncorrectas = 0;
    private int respuestasNoRespondidas = 0;

    private CountDownTimer contador;
    private long tiempoRestante;

    private int cantidad;
    private String categoria;
    private String dificultad;

    private boolean haRespondido = false;
    private boolean timerCorriendo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        textPregunta = findViewById(R.id.tvPregunta);
        textContador = findViewById(R.id.textContador);
        grupoOpciones = findViewById(R.id.radioGroupOpciones);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        cantidad = getIntent().getIntExtra("cantidad", 1);
        categoria = getIntent().getStringExtra("categoria");
        dificultad = getIntent().getStringExtra("dificultad");

        // Tiempo por dificultad
        int tiempoPorPregunta;
        switch (dificultad) {
            case "easy":
                tiempoPorPregunta = 5;
                break;
            case "medium":
                tiempoPorPregunta = 7;
                break;
            case "hard":
                tiempoPorPregunta = 10;
                break;
            default:
                tiempoPorPregunta = 5;
        }

        tiempoRestante = cantidad * tiempoPorPregunta * 1000L;

        obtenerPreguntas();

        btnSiguiente.setOnClickListener(v -> {
            if (!haRespondido) {
                Toast.makeText(this, "Debes seleccionar una respuesta", Toast.LENGTH_SHORT).show();
                return;
            }

            if (indexActual < preguntas.size() - 1) {
                indexActual++;
                mostrarPregunta();
            } else {
                terminarTrivia();
            }
        });
    }

    private void obtenerPreguntas() {
        TriviaApiService.getInstance().obtenerPreguntas(cantidad, categoria, dificultad, "boolean")
                .enqueue(new Callback<TriviaResponse>() {
                    @Override
                    public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                        if (response.isSuccessful()) {
                            preguntas = response.body().getResults();
                            if (preguntas.isEmpty()) {
                                Toast.makeText(TriviaActivity.this, "No se encontraron preguntas", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            mostrarPregunta();
                            iniciarContador();
                        } else {
                            Toast.makeText(TriviaActivity.this, "Error al obtener preguntas", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<TriviaResponse> call, Throwable t) {
                        Toast.makeText(TriviaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void mostrarPregunta() {
        grupoOpciones.clearCheck();
        haRespondido = false;

        Pregunta pregunta = preguntas.get(indexActual);
        textPregunta.setText(pregunta.getPregunta());

        List<String> opciones = new ArrayList<>();
        opciones.add("True");
        opciones.add("False");

        ((RadioButton) grupoOpciones.getChildAt(0)).setText(opciones.get(0));
        ((RadioButton) grupoOpciones.getChildAt(1)).setText(opciones.get(1));

        grupoOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            haRespondido = true;
            RadioButton seleccion = findViewById(checkedId);
            String respuesta = seleccion.getText().toString();

            if (respuesta.equalsIgnoreCase(pregunta.getCorrect_answer())) {
                respuestasCorrectas++;
            } else {
                respuestasIncorrectas++;
            }
        });
    }

    private void iniciarContador() {
        contador = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                actualizarContador();
            }

            @Override
            public void onFinish() {
                respuestasNoRespondidas = cantidad - (respuestasCorrectas + respuestasIncorrectas);
                terminarTrivia();
            }
        }.start();
        timerCorriendo = true;
    }

    private void actualizarContador() {
        int segundos = (int) (tiempoRestante / 1000);
        int minutos = segundos / 60;
        segundos = segundos % 60;

        String tiempo = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        textContador.setText(tiempo);
    }

    private void terminarTrivia() {
        if (contador != null) contador.cancel();

        Intent intent = new Intent(this, EstadisticasActivity.class);
        intent.putExtra("correctas", respuestasCorrectas);
        intent.putExtra("incorrectas", respuestasIncorrectas);
        intent.putExtra("no_respondidas", cantidad - (respuestasCorrectas + respuestasIncorrectas));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (contador != null) contador.cancel();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("tiempoRestante", tiempoRestante);
        outState.putInt("indexActual", indexActual);
        outState.putInt("correctas", respuestasCorrectas);
        outState.putInt("incorrectas", respuestasIncorrectas);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tiempoRestante = savedInstanceState.getLong("tiempoRestante");
        indexActual = savedInstanceState.getInt("indexActual");
        respuestasCorrectas = savedInstanceState.getInt("correctas");
        respuestasIncorrectas = savedInstanceState.getInt("incorrectas");

        mostrarPregunta();
        iniciarContador();
    }
}
