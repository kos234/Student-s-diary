package com.example.kos;

import android.text.SpannableString;

public class ConstrOnStart {
    int Id, item;
    SpannableString s;

    public ConstrOnStart(int id, int item, SpannableString s) {
        Id = id;
        this.item = item;
        this.s = s;
    }

    public ConstrOnStart(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public int getItem() {
        return item;
    }

    public SpannableString getS() {
        return s;
    }
}
