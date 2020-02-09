package com.example.kos;

public class helperDnewnik {
    String NameDay , NamePred, Kab ,Dz, Ocenka;


    public helperDnewnik(String nameDay, String namePred, String kab, String dz, String Ocenka) {
        NameDay = nameDay;
        NamePred = namePred;
        Kab = kab;
        Dz = dz;
        this.Ocenka = Ocenka;
    }

    public String getNameDay() {
        return NameDay;
    }

    public String getNamePred() {
        return NamePred;
    }

    public String getKab() {
        return Kab;
    }

    public String getDz() {
        return Dz;
    }

    public String getOcenka() {
        return Ocenka;
    }
}