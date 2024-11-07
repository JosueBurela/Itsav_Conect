package com.example.itsav_conect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacionActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1; // Código de solicitud para elegir un archivo
    private EditText etTitulo, etDescripcion;
    private Button btnSeleccionarArchivo, btnPublicar;
    private TextView tvFileName; // Agrega un TextView para mostrar el nombre del archivo
    private Uri archivoUri;

    // Variable para almacenar el nombre de usuario
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        // Recuperar el nombre de usuario desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        nombreUsuario = preferences.getString("nombre_usuario", "Usuario"); // Corrige la clave aquí

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnSeleccionarArchivo = findViewById(R.id.btnSelectFile);
        btnPublicar = findViewById(R.id.btnPublicar);
        tvFileName = findViewById(R.id.tvFileName); // Inicializa el TextView


        btnSeleccionarArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicar();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*"); // Permitir cualquier tipo de archivo
        intent.setAction(Intent.ACTION_GET_CONTENT); // Acción para obtener contenido
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            archivoUri = data.getData(); // Obtener el URI del archivo seleccionado
            String fileName = getFileName(archivoUri); // Obtener el nombre del archivo
            tvFileName.setText("Archivo seleccionado: " + fileName); // Mostrar el nombre del archivo
            Toast.makeText(this, "Archivo seleccionado: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    private void publicar() {
        String titulo = etTitulo.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        if (titulo.isEmpty() || descripcion.isEmpty() || archivoUri == null) {
            Toast.makeText(this, "Por favor llena todos los campos y selecciona un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el nombre del archivo
        String fileName = getFileName(archivoUri);

        // Crear RequestBody para el título, la descripción y el nombre de usuario
        RequestBody requestTitulo = RequestBody.create(MediaType.parse("text/plain"), titulo);
        RequestBody requestDescripcion = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        RequestBody requestNombreUsuario = RequestBody.create(MediaType.parse("text/plain"), nombreUsuario);  // Agregar nombre de usuario

        // Crear un RequestBody para el archivo
        RequestBody requestFile;
        try {
            InputStream inputStream = getContentResolver().openInputStream(archivoUri);
            byte[] bytes = getBytes(inputStream); // Método que convierte InputStream a byte[]
            requestFile = RequestBody.create(MultipartBody.FORM, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part body = MultipartBody.Part.createFormData("archivo", fileName, requestFile);

        // Llamada a la API con los cuatro parámetros
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadFile(requestTitulo, requestDescripcion, requestNombreUsuario, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Manejar respuesta exitosa
                    Log.d("Upload", "Successful");
                    Toast.makeText(PublicacionActivity.this, "Publicación realizada", Toast.LENGTH_SHORT).show();
                } else {
                    // Manejar error
                    Log.d("Upload", "Failed: " + response.message());
                    Toast.makeText(PublicacionActivity.this, "Error en la publicación: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Manejar error de red
                Log.d("Upload", "Error: " + t.getMessage());
                Toast.makeText(PublicacionActivity.this, "Error en la publicación: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para obtener el nombre del archivo
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Método para convertir InputStream a byte[]
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
