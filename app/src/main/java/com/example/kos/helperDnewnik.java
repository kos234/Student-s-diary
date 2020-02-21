package com.example.kos;

class helperDnewnik {
    private final String NameDay;
    private final String NamePred;
    private final String Kab;
    private final String Dz;
    private final String Ocenka;


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