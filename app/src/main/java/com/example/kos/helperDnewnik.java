package com.example.kos;


import java.util.ArrayList;

public class helperDnewnik {
    String NameDay;
    String NamePred;
    String Kab;
    String Dz;

    public helperDnewnik(String nameDay, String namePred, String kab, String dz) {
        NameDay = nameDay;
        NamePred = namePred;
        Kab = kab;
        Dz = dz;
    }

    public String getNameDay() {
        return NameDay;
    }

    public void setNameDay(String nameDay) {
        NameDay = nameDay;
    }

    public String getNamePred() {
        return NamePred;
    }

    public void setNamePred(String namePred) {
        NamePred = namePred;
    }

    public String getKab() {
        return Kab;
    }

    public void setKab(String kab) {
        Kab = kab;
    }

    public String getDz() {
        return Dz;
    }

    public void setDz(String dz) {
        Dz = dz;
    }
}

