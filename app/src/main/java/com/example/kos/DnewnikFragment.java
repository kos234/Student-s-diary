package com.example.kos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DnewnikFragment extends Fragment {
    private Context context;
    private TextView dateNedel;

    private List<helperDnewnik> helperDnewniks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dnewnik, container, false);
        Starting(view);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        Date date = new Date();
        int startNedeli = Integer.parseInt(date.toString().substring(8,10));
        String nameMes = date.toString().substring(4,7);
        int startMes = 00;
        int dayInMes = 00;
        int endMes = 00;
        String dayName = date.toString().substring(0,3);
        int endNedeli = 00;
        switch (nameMes){
            case "Jan":
                dayInMes = new Const().Jan;
                startMes = 1;
                endMes = 2;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Feb":
                dayInMes = new Const().Feb;
                startMes = 2;
                endMes = 3;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Mar":
                dayInMes = new Const().Mar;
                startMes = 3;
                endMes = 4;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Apr":
                dayInMes = new Const().Apr;
                startMes = 4;
                endMes = 5;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "May":
                dayInMes = new Const().May;
                startMes = 5;
                endMes = 6;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Jun":
                dayInMes = new Const().Jun;
                startMes = 6;
                endMes = 7;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Jul":
                dayInMes = new Const().Jul;
                startMes = 7;
                endMes = 8;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Aug":
                dayInMes = new Const().Aug;
                startMes = 8;
                endMes = 9;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Sep":
                dayInMes = new Const().Sep;
                startMes = 9;
                endMes = 10;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Oct":
                dayInMes = new Const().Oct;
                startMes = 10;
                endMes = 11;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Nov":
                dayInMes = new Const().Nov;
                startMes = 11;
                endMes = 12;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
            case "Dec":
                dayInMes = new Const().Dec;
                startMes = 12;
                endMes = 1;
                switch (dayName){
                    case "Mon":
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Tue":
                        startNedeli --;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Wed":
                        startNedeli = startNedeli - 2;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Thu":
                        startNedeli = startNedeli - 3;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Fri":
                        startNedeli = startNedeli - 4;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sat":
                        startNedeli = startNedeli - 5;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                    case "Sun":
                        startNedeli = startNedeli - 6;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes )
                            endNedeli = endNedeli - dayInMes;
                        else
                             endMes = startMes;
                        break;
                }
                break;
        }
        dateNedel = view.findViewById(R.id.textViewDnew);
        dateNedel.setText(startNedeli + "." + startMes + " - " + endNedeli + "." + endMes);
        return view;
    }
    /*public String Pochet (){
        String url;


        return url;
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    public void Starting(View view) {

        String[] help, helpKab;
        ArrayList<String> NamePred = new ArrayList<>();
        ArrayList<String> Kab = new ArrayList<>();
        ArrayList<String> Dz = new ArrayList<>();
        try {
            FileInputStream read =  getActivity().openFileInput("Monday.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
                help = temp_read.split("=");

                helpKab = help[1].split(",");
                NamePred.add(helpKab[0]);
                Kab.add(helpKab[1].substring(1));
                Dz.add(help[2]);

            }
            helperDnewniks.add(new helperDnewnik(NamePred,Kab,Dz));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
        ViewPager viewPager = view.findViewById(R.id.viewPagerDnewnik);
        viewPager.setAdapter(pagerAdapterInCard);
        viewPager.setPadding(50, 0, 50, 0);

    }
}
