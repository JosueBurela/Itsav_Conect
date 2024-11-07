package com.example.itsav_conect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView nombreUsuarioTextView;
    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;
    private List<Publicacion> publicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configurar el Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Cargar nombre de usuario desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nombreUsuario = preferences.getString("nombre_usuario", "Usuario");

        // Inicializar el TextView para mostrar el nombre del usuario en la Toolbar
        nombreUsuarioTextView = findViewById(R.id.nombre_usuario_textview); // Asegúrate de que estás usando el ID correcto
        nombreUsuarioTextView.setText(nombreUsuario); // Mostrar solo el nombre

        // Configurar Floating Action Button
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, PublicacionActivity.class);
            startActivity(intent);
        });

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_viewPublicaciones); // Asegúrate de que el ID es correcto
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        publicaciones = new ArrayList<>();
        publicacionAdapter = new PublicacionAdapter(publicaciones);
        recyclerView.setAdapter(publicacionAdapter);

        // Cargar publicaciones al inicio
        cargarPublicaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPublicaciones(); // Recargar publicaciones cada vez que se regrese a la actividad
    }

    private void cargarPublicaciones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponsePublicaciones> call = apiService.getPublicaciones();

        call.enqueue(new Callback<ResponsePublicaciones>() {
            @Override
            public void onResponse(Call<ResponsePublicaciones> call, Response<ResponsePublicaciones> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        publicaciones.clear();
                        publicaciones.addAll(response.body().getPublicaciones());

                        // Invertir el orden de las publicaciones para mostrar las más recientes primero
                        Collections.reverse(publicaciones);

                        publicacionAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "No hay publicaciones disponibles", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePublicaciones> call, Throwable t) {
                // Manejar error
                Toast.makeText(HomeActivity.this, "Error al cargar publicaciones: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
