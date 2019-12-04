package com.example.kos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DnewnikFragment extends Fragment {
    private Context context;
    private TextView dateNedel;
    private SharedPreferences settings;
    private List<helperDnewnik> helperDnewniks = new ArrayList<>();
    LinearLayout linearLayout;
    int startNedeli;
    int startMes;
    int dayInMes;
    int endMes;
    int endNedeli;
    private String nameMes;
    private String dayName;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dnewnik, container, false);
        linearLayout = view.findViewById(R.id.LinerTask);

        new StartAsyncTask().execute();
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        Cliks(view);


        final TextView textView = view.findViewById(R.id.textViewDnew);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setMessage("Очистить вместе с записями домашнего задания расписание?").setCancelable(true).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int l) {
                       new ClearAllAsyncTask().execute();
                    }
                })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int l) {
                       new ClearDzAsyncTask().execute();
                    }
                })
                        .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = deleted.create();
                alertDialog.setTitle("Очиста текущей недели");
                alertDialog.show();
            }
        });

        return view;
    }


    public void Cliks (final View viewRoditel){
        ImageButton imageButtonOne = viewRoditel.findViewById(R.id.imageButtonDnew);
        ImageButton imageButtonTwo = viewRoditel.findViewById(R.id.imageButtonDnew2);
        imageButtonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LeftAsyncTask().execute();
            }
        });
        imageButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RightAsyncTask().execute();
            }
        });
    }

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

    class ClearAllAsyncTask extends AsyncTask<Void,String[],Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            linearLayout.addView(viewPager,layoutParams);
        }

        @Override
        protected Void doInBackground(Void... voids) {
helperDnewniks.clear();
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);
                String nameDay;
                String ulrTwo;

                switch (i){
                    case 0:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = "Вторник";
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = "Среда";
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = "Четверг";
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = "Пятница";
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = "Суббота";
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                }
                    StringBuffer stringBuffer = new StringBuffer();

                    try {
                        FileInputStream read =  getActivity().openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read,helpZapis = "",helpZapis2= "",helpZapis3= "";
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("=\n");
                            helpKab = help[1].split(",");


                            helpZapis = helpZapis  + helpKab[0]+ "=";
                            helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                            helpZapis3 = helpZapis3 + " =";
                        }

                        helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException p) {
                        p.printStackTrace();
                    } catch (IOException a) {
                        a.printStackTrace();
                    }




            }
            return null;
        }
    }

    class LeftAsyncTask extends AsyncTask<Void,String[],Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            linearLayout.addView(viewPager,layoutParams);
            dateNedel = getActivity().findViewById(R.id.textViewDnew);
            dateNedel.setText(startNedeli + "." + startMes + " - " + endNedeli + "." + endMes);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            startNedeli = settings.getInt("StartNedeli",1);
            nameMes = settings.getString("StartMes","Jan");
            startMes = 00;
            dayInMes = 00;
            endMes = 00;
            endNedeli = 00;
            helperDnewniks.clear();
            switch (nameMes){
                case "Jan":
                    startMes = 1;
                    endMes = 12;
                    dayInMes = new Const().Dec;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 12;
                        endMes = 1;
                        editor.putString("StartMes","Dec");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Feb":
                    startMes = 2;
                    endMes = 1;
                    dayInMes = new Const().Jan;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 1;
                        endMes = 2;
                        editor.putString("StartMes","Jan");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Mar":
                    startMes = 3;
                    endMes = 2;
                    int year = settings.getInt("Year",119);
                    if ((year+1900) % 4 == 0 && (year+1900 % 100 != 0) || (year+1900 % 400 == 0))
                        dayInMes = new Const().FebVesok;
                    else
                        dayInMes = new Const().Feb;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 2;
                        endMes = 3;
                        editor.putString("StartMes","Feb");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Apr":
                    startMes = 4;
                    endMes = 3;
                    dayInMes = new Const().Mar;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 3;
                        endMes = 4;
                        editor.putString("StartMes","Mar");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "May":
                    startMes = 5;
                    endMes = 4;
                    dayInMes = new Const().Apr;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 4;
                        endMes = 5;
                        editor.putString("StartMes","Apr");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Jun":
                    startMes = 6;
                    endMes = 5;
                    dayInMes = new Const().May;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 5;
                        endMes = 6;
                        editor.putString("StartMes","May");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Jul":
                    startMes = 7;
                    endMes = 6;
                    dayInMes = new Const().Jun;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 6;
                        endMes = 7;
                        editor.putString("StartMes","Jun");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Aug":
                    startMes = 8;
                    endMes = 7;
                    dayInMes = new Const().Jul;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 7;
                        endMes = 8;
                        editor.putString("StartMes","Jul");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Sep":
                    startMes = 9;
                    endMes = 8;
                    dayInMes = new Const().Aug;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 8;
                        endMes = 9;
                        editor.putString("StartMes","Aug");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Oct":
                    startMes = 10;
                    endMes = 9;
                    dayInMes = new Const().Sep;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 9;
                        endMes = 10;
                        editor.putString("StartMes","Sep");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Nov":
                    startMes = 11;
                    endMes = 10;
                        dayInMes = new Const().Oct;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 10;
                        endMes = 11;
                        editor.putString("StartMes","Oct");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Dec":
                    startMes = 12;
                    endMes = 11;
                    dayInMes = new Const().Nov;
                    startNedeli = startNedeli - 7;
                    if (startNedeli <= 0) {
                        startNedeli = dayInMes + startNedeli;
                        startMes = 11;
                        endMes = 12;
                        editor.putString("StartMes","Nov");

                    }
                    if (startNedeli + 7 >= new Const().Dec)
                        editor.putInt("Year",settings.getInt("Year",119) - 1);
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
            }
            editor.putInt("StartNedeli",startNedeli);
            editor.putInt("IntMes",startMes);
            editor.apply();
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);
                String nameDay;
                String ulrTwo;

                switch (i){
                    case 0:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = "Вторник";
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = "Среда";
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = "Четверг";
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = "Пятница";
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = "Суббота";
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                }
                try {
                    FileInputStream read =  getActivity().openFileInput(url);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read,helpZapis = "", helpZapis2 = "",helpZapis3 = "";
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        helpZapis = helpKab[0]+ "=";
                        helpZapis2 = helpKab[1].substring(1)+ "=";
                        if(2 <= help.length)
                            helpZapis3 = help[1]+ "=";
                        else
                            helpZapis3 = " =";
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        helpZapis = helpZapis  + helpKab[0]+ "=";
                        helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                        if (2 <= help.length)
                            helpZapis3 = helpZapis3 + help[1]+ "=";
                        else
                            helpZapis3 = helpZapis3 + " =";
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuffer stringBuffer = new StringBuffer();

                    try {
                        FileInputStream read =  getActivity().openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read,helpZapis = "",helpZapis2= "",helpZapis3= "";
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("=\n");
                            helpKab = help[1].split(",");


                            helpZapis = helpZapis  + helpKab[0]+ "=";
                            helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                            helpZapis3 = helpZapis3 + " =";
                        }

                        helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException p) {
                        p.printStackTrace();
                    } catch (IOException a) {
                        a.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
    }

    class RightAsyncTask extends AsyncTask<Void,String[],Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            linearLayout.addView(viewPager,layoutParams);
            dateNedel = getActivity().findViewById(R.id.textViewDnew);
            dateNedel.setText(startNedeli + "." + startMes + " - " + endNedeli + "." + endMes);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            startNedeli = settings.getInt("StartNedeli",1);
            nameMes = settings.getString("StartMes","Jan");
            startMes = 0;
            dayInMes = 0;
            endMes = 00;
            endNedeli = 00;
            helperDnewniks.clear();
            switch (nameMes) {
                case "Jan":
                    startMes = 1;
                    endMes = 2;
                        dayInMes = new Const().Jan;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Feb");
                    }
                    if (startNedeli - 7 <= 0)
                        editor.putInt("Year", settings.getInt("Year", 119) + 1);
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes)
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Feb":
                    startMes = 2;
                    endMes = 3;

                    int year = settings.getInt("Year", 119);
                    if ((year + 1900) % 4 == 0 && (year + 1900 % 100 != 0) || (year + 1900 % 400 == 0))
                        dayInMes = new Const().FebVesok;
                    else
                        dayInMes = new Const().Feb;

                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Mar");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Mar":
                    startMes = 3;
                    endMes = 4;
                        dayInMes = new Const().Mar;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Apr");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Apr":
                    startMes = 4;
                    endMes = 5;
                        dayInMes = new Const().Apr;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","May");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "May":
                    startMes = 5;
                    endMes = 6;
                        dayInMes = new Const().May;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Jun");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Jun":
                    startMes = 6;
                    endMes = 7;
                        dayInMes = new Const().Jun;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Jul");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Jul":
                    startMes = 7;
                    endMes = 8;
                        dayInMes = new Const().Jul;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Aug");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Aug":
                    startMes = 8;
                    endMes = 9;
                        dayInMes = new Const().Aug;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Sep");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Sep":
                    startMes = 9;
                    endMes = 10;
                        dayInMes = new Const().Sep;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Oct");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Oct":
                    startMes = 10;
                    endMes = 11;
                        dayInMes = new Const().Oct;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes ;
                        editor.putString("StartMes","Nov");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;

                case "Nov":
                    startMes = 11;
                    endMes = 12;
                        dayInMes = new Const().Nov;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Dec");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
                case "Dec":
                    startMes = 12;
                    endMes = 1;
                        dayInMes = new Const().Dec;
                    startNedeli = startNedeli + 7;
                    if (startNedeli > dayInMes) {
                        startNedeli = startNedeli - dayInMes;
                        startMes = endMes;
                        editor.putString("StartMes","Jan");
                    }
                    endNedeli = startNedeli + 6;
                    if (endNedeli > dayInMes )
                        endNedeli = endNedeli - dayInMes;
                    else
                        endMes = startMes;
                    break;
            }

            editor.putInt("StartNedeli",startNedeli);
            editor.putInt("IntMes",startMes);
            editor.apply();
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);
                String nameDay;
                String ulrTwo;

                switch (i){
                    case 0:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = "Вторник";
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = "Среда";
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = "Четверг";
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = "Пятница";
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = "Суббота";
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                }
                try {
                    FileInputStream read =  getActivity().openFileInput(url);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read,helpZapis = "", helpZapis2 = "",helpZapis3 = "";
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        helpZapis = helpKab[0] + "=";
                        helpZapis2 = helpKab[1].substring(1)+ "=";
                    if(2 <= help.length)
                        helpZapis3 = help[1]+ "=";
                    else
                        helpZapis3 = " =";
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        helpZapis = helpZapis  + helpKab[0]+ "=";
                        helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                        if (2 <= help.length)
                        helpZapis3 = helpZapis3 + help[1]+ "=";
                        else
                            helpZapis3 = helpZapis3 + " =";
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuffer stringBuffer = new StringBuffer();

                    try {
                        FileInputStream read =  getActivity().openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read,helpZapis = "",helpZapis2= "",helpZapis3= "";
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("=\n");
                            helpKab = help[1].split(",");


                            helpZapis = helpZapis  + helpKab[0]+ "=";
                            helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                            helpZapis3 = helpZapis3 + " =";
                        }
                        helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException p) {
                        p.printStackTrace();
                    } catch (IOException a) {
                        a.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }
    }
    class ClearDzAsyncTask extends AsyncTask<Void,String[],Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            linearLayout.addView(viewPager,layoutParams);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helperDnewniks.clear();
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);
                String nameDay;

                switch (i){
                    case 1:
                        nameDay = "Вторник";
                        break;
                    case 2:
                        nameDay = "Среда";
                        break;
                    case 3:
                        nameDay = "Четверг";
                        break;
                    case 4:
                        nameDay = "Пятница";
                        break;
                    case 5:
                        nameDay = "Суббота";
                        break;

                    default:
                        nameDay = "Понедельник";
                        break;
                }
                StringBuffer stringBuffer = new StringBuffer();
                try {
                    FileInputStream read =  getActivity().openFileInput(url);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read,helpZapis = "", helpZapis2 = "",helpZapis3 = "";
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        helpZapis = helpKab[0]+ "=";
                        helpZapis2 = helpKab[1].substring(1)+ "=";
                        helpZapis3 = helpZapis3 + " =";
                        stringBuffer.append(help[0]).append("=\n");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        helpZapis = helpZapis  + helpKab[0]+ "=";
                        helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                        helpZapis3 = helpZapis3 + " =";
                        stringBuffer.append(help[0]).append("=\n");
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));

                } catch (FileNotFoundException e) {}
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                    String temp_write = stringBuffer.toString();

                    write.write(temp_write.getBytes());
                    write.close();
                } catch (FileNotFoundException p) {
                    p.printStackTrace();
                } catch (IOException a) {
                    a.printStackTrace();
                }
            }
            return null;
        }
    }

    class StartAsyncTask extends AsyncTask<Void,String[],Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.setId(1234626486);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            Date start = new Date();
            switch (start.toString().substring(0,3)) {
                case "Tue":
                    viewPager.setCurrentItem(1);
                    break;
                case "Wed":
                    viewPager.setCurrentItem(2);
                    break;
                case "Thu":
                    viewPager.setCurrentItem(3);
                    break;
                case "Fri":
                    viewPager.setCurrentItem(4);
                    break;
                case "Sat":
                    viewPager.setCurrentItem(5);
                    break;
                default:
                    viewPager.setCurrentItem(0);
                    break;
            }
            linearLayout.addView(viewPager,layoutParams);
            dateNedel = getActivity().findViewById(R.id.textViewDnew);
            dateNedel.setText(startNedeli + "." + startMes + " - " + endNedeli + "." + endMes);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            Date date = new Date();
            startNedeli = Integer.parseInt(date.toString().substring(8,10));
            nameMes = date.toString().substring(4,7);
            startMes = 00;
            dayInMes = 00;
            endMes = 00;
            dayName = date.toString().substring(0,3);
            endNedeli = 00;
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
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 12;
                                endMes = 1;
                                startNedeli = new Const().Dec + startNedeli;
                                dayInMes = new Const().Dec;
                                nameMes = "Dec";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 1;
                                endMes = 2;
                                startNedeli = new Const().Jan + startNedeli;
                                dayInMes = new Const().Jan;
                                nameMes = "Jan";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 2;
                                endMes = 3;
                                startNedeli = new Const().Feb + startNedeli;
                                dayInMes = new Const().Feb;
                                nameMes = "Feb";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 3;
                                endMes = 4;
                                startNedeli = new Const().Mar + startNedeli;
                                dayInMes = new Const().Mar;
                                nameMes = "Mar";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 4;
                                endMes = 5;
                                startNedeli = new Const().Apr + startNedeli;
                                dayInMes = new Const().Apr;
                                nameMes = "Apr";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 5;
                                endMes = 6;
                                startNedeli = new Const().May + startNedeli;
                                dayInMes = new Const().May;
                                nameMes = "May";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 6;
                                endMes = 7;
                                startNedeli = new Const().Jun + startNedeli;
                                dayInMes = new Const().Jun;
                                nameMes = "Jun";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 7;
                                endMes = 8;
                                startNedeli = new Const().Jul + startNedeli;
                                dayInMes = new Const().Jul;
                                nameMes = "Jul";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 7;
                                endMes = 8;
                                startNedeli = new Const().Jul + startNedeli;
                                dayInMes = new Const().Jul;
                                nameMes = "Jul";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 7;
                                endMes = 8;
                                startNedeli = new Const().Jul + startNedeli;
                                dayInMes = new Const().Jul;
                                nameMes = "Jul";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 7;
                                endMes = 8;
                                startNedeli = new Const().Jul + startNedeli;
                                dayInMes = new Const().Jul;
                                nameMes = "Jul";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 7;
                                endMes = 8;
                                startNedeli = new Const().Jul + startNedeli;
                                dayInMes = new Const().Jul;
                                nameMes = "Jul";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                                if (startNedeli <= 0) {
                                    startMes = 7;
                                    endMes = 8;
                                    startNedeli = new Const().Jul + startNedeli;
                                    dayInMes = new Const().Jul;
                                    nameMes = "Jul";
                                }
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
                            if (startNedeli <= 0) {
                                startMes = 8;
                                endMes = 9;
                                startNedeli = new Const().Aug + startNedeli;
                                dayInMes = new Const().Aug;
                                nameMes = "Aug";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 8;
                                endMes = 9;
                                startNedeli = new Const().Aug + startNedeli;
                                dayInMes = new Const().Aug;
                                nameMes = "Aug";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 8;
                                endMes = 9;
                                startNedeli = new Const().Aug + startNedeli;
                                dayInMes = new Const().Aug;
                                nameMes = "Aug";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 8;
                                endMes = 9;
                                startNedeli = new Const().Aug + startNedeli;
                                dayInMes = new Const().Aug;
                                nameMes = "Aug";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 8;
                                endMes = 9;
                                startNedeli = new Const().Aug + startNedeli;
                                dayInMes = new Const().Aug;
                                nameMes = "Aug";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                                if (startNedeli <= 0) {
                                    startMes = 8;
                                    endMes = 9;
                                    startNedeli = new Const().Aug + startNedeli;
                                    dayInMes = new Const().Aug;
                                    nameMes = "Aug";
                                }
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
                            if (startNedeli <= 0) {
                                startMes = 9;
                                endMes = 10;
                                startNedeli = new Const().Sep + startNedeli;
                                dayInMes = new Const().Sep;
                                nameMes = "Sep";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 9;
                                endMes = 10;
                                startNedeli = new Const().Sep + startNedeli;
                                dayInMes = new Const().Sep;
                                nameMes = "Sep";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes)
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                                if (startNedeli <= 0) {
                                    startMes = 9;
                                    endMes = 10;
                                    startNedeli = new Const().Sep + startNedeli;
                                    dayInMes = new Const().Sep;
                                    nameMes = "Sep";
                                }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 9;
                                endMes = 10;
                                startNedeli = new Const().Sep + startNedeli;
                                dayInMes = new Const().Sep;
                                nameMes = "Sep";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 9;
                                endMes = 10;
                                startNedeli = new Const().Sep + startNedeli;
                                dayInMes = new Const().Sep;
                                nameMes = "Sep";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 9;
                                endMes = 10;
                                startNedeli = new Const().Sep + startNedeli;
                                dayInMes = new Const().Sep;
                                nameMes = "Sep";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 10;
                                endMes = 11;
                                startNedeli = new Const().Oct + startNedeli;
                                dayInMes = new Const().Oct;
                                nameMes = "Oct";
                            }
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
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";

                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Wed":
                            startNedeli = startNedeli - 2;
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Thu":
                            startNedeli = startNedeli - 3;
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Fri":
                            startNedeli = startNedeli - 4;
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sat":
                            startNedeli = startNedeli - 5;
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                        case "Sun":
                            startNedeli = startNedeli - 6;
                            if (startNedeli <= 0) {
                                startMes = 11;
                                endMes = 12;
                                startNedeli = new Const().Nov + startNedeli;
                                dayInMes = new Const().Nov;
                                nameMes = "Nov";
                            }
                            endNedeli = startNedeli + 6;
                            if (endNedeli > dayInMes )
                                endNedeli = endNedeli - dayInMes;
                            else
                                endMes = startMes;
                            break;
                    }
                    break;
            }
            editor.putInt("StartNedeli",startNedeli);
            editor.putString("StartMes",nameMes);
            editor.putInt("IntMes",startMes);
            editor.putInt("Year",date.getYear());
            editor.apply();
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + date.getYear();
                String nameDay = "Понедельник";
                String ulrTwo = "Monday.txt";

                switch (i){
                    case 0:
                        ulrTwo = "Monday.txt";
                        nameDay = "Понедельник";
                        break;
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = "Вторник";
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = "Среда";
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = "Четверг";
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = "Пятница";
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = "Суббота";
                        break;

                }
                try {
                    FileInputStream read =  getActivity().openFileInput(url);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read,helpZapis = "", helpZapis2 = "",helpZapis3 = "";
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        helpZapis = helpKab[0]+ "=";
                        helpZapis2 = helpKab[1].substring(1)+ "=";
                        if(2 <= help.length)
                            helpZapis3 = help[1]+ "=";
                        else
                            helpZapis3 = " =";
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        helpZapis = helpZapis  + helpKab[0]+ "=";
                        helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                        if (2 <= help.length)
                            helpZapis3 = helpZapis3 + help[1]+ "=";
                        else
                            helpZapis3 = helpZapis3 + " =";
                    }

                    helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuffer stringBuffer = new StringBuffer();

                    try {
                        FileInputStream read =  getActivity().openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read,helpZapis = "",helpZapis2= "",helpZapis3= "";
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("=\n");
                            helpKab = help[1].split(",");


                            helpZapis = helpZapis  + helpKab[0]+ "=";
                            helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                            helpZapis3 = helpZapis3 + " =";
                        }
                        helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException p) {
                        p.printStackTrace();
                    } catch (IOException a) {
                        a.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }



    }

}
