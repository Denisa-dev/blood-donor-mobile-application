package com.example.firestoredatabase;

import java.util.Date;

public class Users {
    private String nume;
    private String adresa;
    private Integer varsta;
    private String telefon;
    private String grupaSange;
    private String email;
    private Date dataDonare;

    public Users() {
    }

    public Users(String nume, String adresa, Integer varsta, String telefon, String grupaSange, String email, Date dataDonare) {
        this.nume = nume;
        this.adresa = adresa;
        this.varsta = varsta;
        this.telefon = telefon;
        this.grupaSange = grupaSange;
        this.email = email;
        this.dataDonare = dataDonare;
    }

    public String getNume() {
        return nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public Integer getVarsta() {
        return varsta;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getGrupaSange() {
        return grupaSange;
    }

    public String getEmail() {
        return email;
    }

    public Date getDataDonare() {
        return dataDonare;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setGrupaSange(String grupaSange) {
        this.grupaSange = grupaSange;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDataDonare(Date dataDonare) {
        this.dataDonare = dataDonare;
    }
}
