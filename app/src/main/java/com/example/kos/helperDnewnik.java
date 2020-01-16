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

    public String getNamePred() {
        return NamePred;
    }

    public String getKab() {
        return Kab;
    }

    public String getDz() {
        return Dz;
    }
}

