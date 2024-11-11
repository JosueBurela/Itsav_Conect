package com.example.itsav_conect;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {

    private List<Publicacion> publicaciones;

    public PublicacionAdapter(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicacion publicacion = publicaciones.get(position);

        holder.usuarioTextView.setText(publicacion.getUsuario());
        holder.tituloTextView.setText(publicacion.getTitulo());
        holder.descripcionTextView.setText(publicacion.getDescripcion());

        // Generar la URL del archivo
        String urlArchivo = "http://192.168.1.64:80/Burela/" + publicacion.getArchivo();

        if (urlArchivo != null && !urlArchivo.isEmpty()) {
            // Comprobar la extensión del archivo para determinar cómo mostrarlo
            if (publicacion.getArchivo().endsWith(".jpg") || publicacion.getArchivo().endsWith(".jpeg") ||
                    publicacion.getArchivo().endsWith(".png") || publicacion.getArchivo().endsWith(".webp")) {
                // Mostrar una imagen
                holder.archivoImageView.setVisibility(View.VISIBLE);
                holder.archivoVideoView.setVisibility(View.GONE);
                Glide.with(holder.itemView.getContext())
                        .load(urlArchivo)
                        .into(holder.archivoImageView);
            } else if (publicacion.getArchivo().endsWith(".mp4")) {
                // Mostrar un video
                holder.archivoVideoView.setVisibility(View.VISIBLE);
                holder.archivoImageView.setVisibility(View.GONE);
                holder.archivoVideoView.setVideoURI(Uri.parse(urlArchivo));
                holder.archivoVideoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    holder.archivoVideoView.start();
                });

                // Manejar errores al reproducir el video
                holder.archivoVideoView.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(holder.itemView.getContext(), "Error al reproducir el video", Toast.LENGTH_SHORT).show();
                    return true;
                });
            } else {
                holder.archivoImageView.setVisibility(View.GONE);
                holder.archivoVideoView.setVisibility(View.GONE);
            }
        } else {
            holder.archivoImageView.setVisibility(View.GONE);
            holder.archivoVideoView.setVisibility(View.GONE);
        }

        // Agregar un click listener para abrir la actividad de detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PublicacionDetailActivity.class);
            intent.putExtra("publicacion_id", publicacion.getId()); // Enviar el ID de la publicación
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    public class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView usuarioTextView;
        ImageView archivoImageView;
        VideoView archivoVideoView;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            usuarioTextView = itemView.findViewById(R.id.usuarioTextView);
            archivoImageView = itemView.findViewById(R.id.archivoImageView);
            archivoVideoView = itemView.findViewById(R.id.archivoVideoView);
        }
    }
}
