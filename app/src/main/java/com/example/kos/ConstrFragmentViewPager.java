package com.example.kos;

import android.view.View;

import java.util.ArrayList;

class ConstrFragmentViewPager {

  private final ArrayList<ConstrRecyclerView> products;
  private final String url;
  private final RecyclerAdapter recyclerAdapter;
  private View view;

  public ConstrFragmentViewPager(ArrayList<ConstrRecyclerView> products, String url, RecyclerAdapter recyclerAdapter) {
    this.products = products;
    this.url = url;
    this.recyclerAdapter = recyclerAdapter;
  }

  public ArrayList<ConstrRecyclerView> getArray() {
    return products;
  }

  public RecyclerAdapter getRecyclerAdapter() {
    return recyclerAdapter;
  }

  public View getView() {
    return view;
  }

  public void setView(View view) {
    this.view = view;
  }

  public String getUrl() {
    return url;
  }
}