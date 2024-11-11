package com.example.itsav_conect;

public class Comentario {
    private int idUsuario;
    private String comentario;
    private String fecha;

    // Constructor, getters y setters

    public Comentario(int idUsuario, String comentario, String fecha) {
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
