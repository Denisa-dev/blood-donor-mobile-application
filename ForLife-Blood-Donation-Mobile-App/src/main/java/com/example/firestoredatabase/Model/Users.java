package com.example.firestoredatabase.Model;

import java.util.Date;

public class Users {
    private String nume;
    private Integer varsta;
    private String telefon;
    private String grupaSange;
    private String email;
    private String judet;
    private String sex;
    private Date dataDonare;
    private Integer noBookings;
    private Integer greutate;

    public Users() {
    }

    public Users(String nume, Integer varsta, String telefon, String grupaSange, String email, String judet, String sex, Date dataDonare, Integer noBookings, Integer greutate) {
        this.nume = nume;
        this.varsta = varsta;
        this.telefon = telefon;
        this.grupaSange = grupaSange;
        this.email = email;
        this.judet = judet;
        this.sex = sex;
        this.dataDonare = dataDonare;
        this.noBookings = noBookings;
        this.greutate = greutate;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Integer getVarsta() {
        return varsta;
    }

    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getGrupaSange() {
        return grupaSange;
    }

    public void setGrupaSange(String grupaSange) {
        this.grupaSange = grupaSange;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDataDonare() {
        return dataDonare;
    }

    public void setDataDonare(Date dataDonare) {
        this.dataDonare = dataDonare;
    }

    public Integer getNoBookings() {
        return noBookings;
    }

    public void setNoBookings(Integer noBookings) {
        this.noBookings = noBookings;
    }

    public Integer getGreutate() {
        return greutate;
    }

    public void setGreutate(Integer greutate) {
        this.greutate = greutate;
    }
}
