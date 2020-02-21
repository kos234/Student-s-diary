package com.example.kos;

class ConstrRecyclerView {

  private String TextName;
    private String TextBottom;

  public ConstrRecyclerView(String textName, String textBottom) {
    TextName = textName;
    TextBottom = textBottom;
  }

  public void changeText(String textNameEdit, String textBottomEdit){
    TextName = textNameEdit;
    TextBottom = textBottomEdit;
  }

  public String getTextName() {
    return TextName;
  }

  public String getTextBottom() {
    return TextBottom;
  }
}