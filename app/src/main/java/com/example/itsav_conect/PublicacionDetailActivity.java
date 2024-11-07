package com.example.itsav_conect;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PublicacionDetailActivity extends AppCompatActivity {

    private TextView tituloTextView;
    private TextView descripcionTextView;
    private TextView usuarioTextView;
    private ImageView archivoImageView;
    private VideoView archivoVideoView;
    private EditText comentarioEditText;
    private Button publicarButton;
    private int publicacionId; // ID de publicación
    private int usuarioId = 123; // Asigna el ID de usuario aquí o recíbelo desde el Intent

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

        publicacionId = getIntent().getIntExtra("publicacion_id", -1);
        if (publicacionId != -1) {
            buscarPublicacion(publicacionId);
        } else {
            Toast.makeText(this, "ID de publicación no válido", Toast.LENGTH_SHORT).show();
        }

        // Configurar el evento del botón publicar
        publicarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = comentarioEditText.getText().toString();
                if (comentario.isEmpty()) {
                    Toast.makeText(PublicacionDetailActivity.this, "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
                } else {
                    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    agregarComentario(publicacionId, usuarioId, comentario, fecha);
                }
            }
        });
    }

    private void agregarComentario(int publicacionId, int usuarioId, String comentario, String fecha) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.agregarComentario(publicacionId, usuarioId, comentario, fecha);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PublicacionDetailActivity.this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                    comentarioEditText.setText(""); // Limpiar el campo de texto
                } else {
                    Toast.makeText(PublicacionDetailActivity.this, "Error al agregar comentario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PublicacionDetailActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarPublicacion(int publicacionId) {
        String url = "http://192.168.1.245:80/Burela/buscar_publicacion.php?id=" + publicacionId;

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
        String urlArchivo = "http://192.168.1.245:80/Burela/" + archivo;

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
}
