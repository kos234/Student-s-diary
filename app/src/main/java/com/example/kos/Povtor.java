package com.example.kos;

import android.os.Build;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.N)
class Povtor extends Exception {

    public Povtor(String message) {
    super(message);
    }
}