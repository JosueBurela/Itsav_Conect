package com.example.itsav_conect;

public class Publicacion {
    private int id;
    private String titulo;
    private String descripcion;
    private String archivo;
    private String usuario;

    public Publicacion(int id, String titulo, String descripcion, String archivo, String usuario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.archivo = archivo;
        this.usuario = usuario;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getArchivo() {
        return archivo;
    }

    public String getUsuario() {
        return usuario;
    }
}
