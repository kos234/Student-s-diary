package com.example.kos;

import android.widget.TableRow;

public class CheakConvenorResult {
  TableRow tableRow;
  int stolbID;

  public CheakConvenorResult(TableRow tableRow, int stolbID) {
    this.tableRow = tableRow;
    this.stolbID = stolbID;
  }

  public TableRow getTableRow() {
    return tableRow;
  }

  public int getStolbID() {
    return stolbID;
  }
}