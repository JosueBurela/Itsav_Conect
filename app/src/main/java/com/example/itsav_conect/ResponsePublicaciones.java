package com.example.itsav_conect;

import java.util.List;

public class ResponsePublicaciones {
    private String status;
    private List<Publicacion> publicaciones;

    public String getStatus() {
        return status;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    // Método para buscar una publicación por ID
    public Publicacion buscarPublicacionPorId(int id) {
        if (publicaciones != null) {
            for (Publicacion publicacion : publicaciones) {
                if (publicacion.getId() == id) {
                    return publicacion;
                }
            }
        }
        return null; // Si no encuentra la publicación, retorna null
    }
}
