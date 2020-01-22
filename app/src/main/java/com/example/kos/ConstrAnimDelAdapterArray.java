package com.example.kos;

import java.util.ArrayList;

public class ConstrAnimDelAdapterArray {
    ArrayList<ConstrRecyclerView> arrayList;
    RecyclerAdapter adapter;

    public ConstrAnimDelAdapterArray(ArrayList<ConstrRecyclerView> arrayList, RecyclerAdapter adapter) {
        this.arrayList = arrayList;
        this.adapter = adapter;
    }

    public ArrayList<ConstrRecyclerView> getArrayList() {
        return arrayList;
    }

    public RecyclerAdapter getAdapter() {
        return adapter;
    }
}
