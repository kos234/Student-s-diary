package com.example.kos;

import android.text.SpannableString;

class ConstrOnStart {
  private final int Id;
    private int item;
  private SpannableString s;

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