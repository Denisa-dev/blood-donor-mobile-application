package com.example.firestoredatabase.Model;

public class TestUser {
    private String nume;
    private String adresa;
    private Integer varsta;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Integer getVarsta() {
        return varsta;
    }

    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
    }

    public TestUser(String nume, String adresa, Integer varsta) {
        this.nume = nume;
        this.adresa = adresa;
        this.varsta = varsta;
    }
}
