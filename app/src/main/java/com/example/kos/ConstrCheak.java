package com.example.kos;

import android.widget.TableLayout;

import java.util.ArrayList;

public class ConstrCheak {
    ArrayList<String[]> arrayList;
    TableLayout tableLayout;

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
