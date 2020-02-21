package com.example.kos;

import android.widget.TableRow;

class CheakConvenorResult {
  private final TableRow tableRow;
  private final int stolbID;

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