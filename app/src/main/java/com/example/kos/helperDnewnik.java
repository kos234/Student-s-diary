package com.example.kos;


import java.util.ArrayList;

public class helperDnewnik {
    ArrayList<String> NamePred;
    ArrayList<String> Kab;
    ArrayList<String> Dz;

    public helperDnewnik(ArrayList<String> namePred, ArrayList<String> kab, ArrayList<String> dz) {
        NamePred = namePred;
        Kab = kab;
        Dz = dz;
    }

    public ArrayList<String> getNamePred() {
        return NamePred;
    }

    public void setNamePred(ArrayList<String> namePred) {
        NamePred = namePred;
    }

    public ArrayList<String> getKab() {
        return Kab;
    }

    public void setKab(ArrayList<String> kab) {
        Kab = kab;
    }

    public ArrayList<String> getDz() {
        return Dz;
    }

    public void setDz(ArrayList<String> dz) {
        Dz = dz;
    }
}

