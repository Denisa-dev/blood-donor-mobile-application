package com.example.firestoredatabase.Model;

public class SpecialCases {
    private String nume;
    private String grupa;
    private String descriere;
    private String judet;
    private String centru;
    private String imageUrl;
    private Integer nrBookings;

    public SpecialCases(){}

    public SpecialCases(String nume, String grupa, String descriere, String judet, String centru, String imageUrl, Integer nrBookings) {
        this.nume = nume;
        this.grupa = grupa;
        this.descriere = descriere;
        this.judet = judet;
        this.centru = centru;
        this.imageUrl = imageUrl;
        this.nrBookings = nrBookings;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public String getCentru() {
        return centru;
    }

    public void setCentru(String centru) {
        this.centru = centru;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getNrBookings() {
        return nrBookings;
    }

    public void setNrBookings(Integer nrBookings) {
        this.nrBookings = nrBookings;
    }
}
