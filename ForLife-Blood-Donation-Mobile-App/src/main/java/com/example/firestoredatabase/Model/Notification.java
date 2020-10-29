package com.example.firestoredatabase.Model;

import java.util.Date;

public class Notification {
    private String titlu;
    private String descriere;
    private String grupa;
    private String centru;
    private String judet;
    private Date data;

    public Notification(){}

    public Notification(String titlu, String descriere, String grupa, String centru, String judet, Date data) {
        this.titlu = titlu;
        this.descriere = descriere;
        this.grupa = grupa;
        this.centru = centru;
        this.judet = judet;
        this.data = data;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getCentru() {
        return centru;
    }

    public void setCentru(String centru) {
        this.centru = centru;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
