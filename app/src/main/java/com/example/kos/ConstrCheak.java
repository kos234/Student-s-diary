package com.example.kos;

import android.widget.TableLayout;

import java.util.ArrayList;

class ConstrCheak {
  private final ArrayList<String[]> arrayList;
  private final TableLayout tableLayout;

  public ConstrCheak(ArrayList<String[]> arrayList, TableLayout tableLayout) {
    this.arrayList = arrayList;
    this.tableLayout = tableLayout;
  }

  public ArrayList<String[]> getArrayList() {
    return arrayList;
  }

  public TableLayout getTableLayout() {
    return tableLayout;
  }
}