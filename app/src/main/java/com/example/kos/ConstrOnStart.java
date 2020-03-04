package com.example.kos;

import android.text.SpannableString;

class ConstrOnStart {
  private final int Id;
    private int item = 0;
    private int visibly = 0;
  private SpannableString s = SpannableString.valueOf("null");

  public ConstrOnStart(int id, int item, SpannableString s) {
    Id = id;
    this.item = item;
    this.s = s;
  }

  public ConstrOnStart(int id) {
    Id = id;
  }
  public ConstrOnStart(int id, int visibly) {
    Id = id;
    this.visibly = visibly;
  }

  public int getId() {
    return Id;
  }

  public int getVisibly() {
    return visibly;
  }

  public int getItem() {
    return item;
  }

  public SpannableString getS() {
    return s;
  }
}