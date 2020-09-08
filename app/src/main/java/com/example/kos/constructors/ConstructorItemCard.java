package com.example.kos.constructors;

public class ConstructorItemCard {
    public final String name;
    public final String kab;
    public final String dz;
    public final String ocenka;
    public final boolean isUmply;
    public final int item;
    public final String[] idTextView;

    public ConstructorItemCard(String name, String kab, String dz, String ocenka, boolean isUmply, int item, String[] idTextView) {
        this.name = name;
        this.kab = kab;
        this.dz = dz;
        this.ocenka = ocenka;
        this.isUmply = isUmply;
        this.item = item;
        this.idTextView = idTextView;
    }
}