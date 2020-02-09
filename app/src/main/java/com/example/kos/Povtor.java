package com.example.kos;

import android.os.Build;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.N)
public class Povtor extends Exception {

  private int cho;

  public Povtor(String message, int cho) {
    super(message);
    this.cho=cho;
  }
}