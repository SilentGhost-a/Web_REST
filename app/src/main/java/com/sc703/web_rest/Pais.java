package com.sc703.web_rest;

public class Pais {
    Integer ID;
    String Nombre;

    public Pais(Integer ID, String nombre) {
        this.ID = ID;
        Nombre = nombre;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}
