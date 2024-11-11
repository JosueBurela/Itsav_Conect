package com.example.itsav_conect;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PublicacionDetailActivity extends AppCompatActivity {

    private TextView tituloTextView;
    private TextView descripcionTextView;
    private TextView usuarioTextView;
    private ImageView archivoImageView;
    private VideoView archivoVideoView;
    private EditText comentarioEditText;
    private Button publicarButton;
    private int publicacionId; // ID de la publicación a obtener
    private String usuarioId; // ID de usuario recuperado de SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion_detail);

        tituloTextView = findViewById(R.id.tituloTextView);
        descripcionTextView = findViewById(R.id.descripcionTextView);
        usuarioTextView = findViewById(R.id.usuarioTextView);
        archivoImageView = findViewById(R.id.archivoImageView);
        archivoVideoView = findViewById(R.id.archivoVideoView);
        comentarioEditText = findViewById(R.id.comentarioEditText);
        publicarButton = findViewById(R.id.publicarButton);

        // Obtener el ID de usuario desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        usuarioId = preferences.getString("id_usuario", null); // Aquí obtenemos el "id_usuario"

        publicacionId = getIntent().getIntExtra("publicacion_id", -1);
        if (publicacionId != -1) {
            buscarPublicacion(publicacionId);
        } else {
            Toast.makeText(this, "ID de publicación no válido", Toast.LENGTH_SHORT).show();
        }

        // Evento del botón publicar
        publicarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturar el comentario ingresado
                String comentario = comentarioEditText.getText().toString();

                // Obtener la fecha del dispositivo (formato "dd MM yyyy")
                SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                String fecha = sdf.format(new Date());

                // Enviar los datos al servidor
                if (!comentario.isEmpty()) {
                    publicarComentario(usuarioId, comentario, fecha);
                } else {
                    Toast.makeText(PublicacionDetailActivity.this, "Por favor ingrese un comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void buscarPublicacion(int publicacionId) {
        String url = "http://192.168.1.64:80/Burela/buscar_publicacion.php?id=" + publicacionId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                JSONObject publicacionJson = response.getJSONObject("publicacion");

                                String titulo = publicacionJson.getString("titulo");
                                String descripcion = publicacionJson.getString("descripcion");
                                String usuario = publicacionJson.getString("usuario");
                                String archivo = publicacionJson.getString("archivo");

                                tituloTextView.setText(titulo);
                                descripcionTextView.setText(descripcion);
                                usuarioTextView.setText(usuario);

                                mostrarArchivo(archivo);
                            } else {
                                Toast.makeText(PublicacionDetailActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PublicacionDetailActivity.this, "Error al procesar la respuesta: " + response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PublicacionDetailActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void mostrarArchivo(String archivo) {
        String urlArchivo = "http://192.168.1.64:80/Burela/" + archivo;

        if (archivo != null && !archivo.isEmpty()) {
            if (archivo.endsWith(".jpg") || archivo.endsWith(".jpeg") || archivo.endsWith(".png") || archivo.endsWith(".webp")) {
                archivoImageView.setVisibility(View.VISIBLE);
                archivoVideoView.setVisibility(View.GONE);
                Glide.with(this)
                        .load(urlArchivo)
                        .into(archivoImageView);
            } else if (archivo.endsWith(".mp4")) {
                archivoVideoView.setVisibility(View.VISIBLE);
                archivoImageView.setVisibility(View.GONE);
                archivoVideoView.setVideoURI(Uri.parse(urlArchivo));
                archivoVideoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    archivoVideoView.start();
                });

                archivoVideoView.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(this, "Error al reproducir el video", Toast.LENGTH_SHORT).show();
                    return true;
                });
            } else {
                archivoImageView.setVisibility(View.GONE);
                archivoVideoView.setVisibility(View.GONE);
            }
        } else {
            archivoImageView.setVisibility(View.GONE);
            archivoVideoView.setVisibility(View.GONE);
        }
    }

    private void publicarComentario(String idUsuario, String comentario, String fecha) {
        String url = "http://192.168.1.64:80/Burela/comentar.php";

        // Verificar los datos antes de enviarlos
        Log.d("PublicarComentario", "idUsuario: " + idUsuario);
        Log.d("PublicarComentario", "comentario: " + comentario);
        Log.d("PublicarComentario", "fecha: " + fecha);

        // Crear una solicitud POST usando StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Intentamos parsear la respuesta como JSON
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String status = jsonResponse.getString("status");
                                if (status.equals("success")) {
                                    Toast.makeText(PublicacionDetailActivity.this, "Comentario publicado exitosamente", Toast.LENGTH_SHORT).show();
                                    comentarioEditText.setText("");
                                } else {
                                    Toast.makeText(PublicacionDetailActivity.this, "Error al publicar el comentario", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                // Si la respuesta no es un JSON, la tratamos como texto plano
                                Toast.makeText(PublicacionDetailActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PublicacionDetailActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(PublicacionDetailActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Crear el mapa de parámetros
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", idUsuario);
                parametros.put("comentario", comentario);
                parametros.put("fecha", fecha);
                return parametros;
            }
        };

        // Crear la cola de solicitudes y agregar la solicitud
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
