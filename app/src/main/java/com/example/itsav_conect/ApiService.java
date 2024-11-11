package com.example.itsav_conect;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @Multipart
    @POST("Burela/upload.php") // Cambia esto según la estructura de tu API
    Call<ResponseBody> uploadFile(
            @Part("titulo") RequestBody titulo,
            @Part("descripcion") RequestBody descripcion,
            @Part("usuario") RequestBody usuario, // Agregar nuevo parámetro
            @Part MultipartBody.Part archivo
    );

    @GET("Burela/mostrar_publicaciones.php") // Llama al archivo PHP correcto
    Call<ResponsePublicaciones> getPublicaciones();

    @GET("Burela/buscar_publicacion.php") // Cambia esto según tu estructura de API
    Call<Publicacion> getPublicacionPorId(@Query("id") int id); // Este método ya está bien

    // Método para enviar el comentario
    @FormUrlEncoded
    @POST("Burela/comentar.php") // Cambia esto a la URL correcta del script PHP
    Call<ResponseBody> agregarComentario(
            @Field("idUsuario") int idUsuario,
            @Field("comentario") String comentario,
            @Field("fecha") String fecha
    );


}
