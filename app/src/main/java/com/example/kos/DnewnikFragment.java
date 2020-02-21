package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
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
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class DnewnikFragment extends Fragment {
    private Context context;
    private TextView dateNedel;
    private final List<helperDnewnik> helperDnewniks = new ArrayList<>();
    private LinearLayout linearLayout;
    private int startNedeli;
    private int startMes;
    private int dayInMes;
    private int endMes;
    private int endNedeli;
    private PagerAdapterInCard pagerAdapterInCard;
    private ViewPager viewPager;
    private SharedPreferences Current_Theme;
    private String nameMes;
    private SharedPreferences settings;
    private View view;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dnewnik, container, false);
        linearLayout = view.findViewById(R.id.LinerTask);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        new StartAsyncTask().execute();
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar3);
        Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
        menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(menuToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) Objects.requireNonNull(getActivity())).openDrawer();
            }
        });
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
        Cliks(view);

        final TextView textView = view.findViewById(R.id.textViewDnew);
        textView.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);
                final AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setView(promptsView);
                deleted.setCancelable(true);
                final AlertDialog alertDialog = deleted.create();
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

                promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

                TextView ButtonClearAll = promptsView.findViewById(R.id.button_three_alert);
                ButtonClearAll.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonClearAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ClearAllAsyncTask().execute();
                        alertDialog.hide();
                    }
                });

                TextView textTitle = promptsView.findViewById(R.id.title_alert);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

                TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                TextView ButtonClearHomework = promptsView.findViewById(R.id.button_two_alert);
                ButtonClearHomework.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ClearDzAsyncTask().execute();
                        alertDialog.hide();
                    }
                });
                ButtonClearHomework.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

                TextView ButtonClearOcenki = promptsView.findViewById(R.id.button_one_alert);
                ButtonClearOcenki.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ClearOcenkiAsyncTask().execute();
                        alertDialog.hide();
                    }
                });
                ButtonClearOcenki.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        return view;
    }


    private void Cliks(final View viewRoditel){
        ImageButton imageButtonOne = viewRoditel.findViewById(R.id.imageButtonDnew);
        imageButtonOne.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
        ImageButton imageButtonTwo = viewRoditel.findViewById(R.id.imageButtonDnew2);
        imageButtonTwo.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
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
        final SharedPreferences.Editor editor = settings.edit();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
             viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
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
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;

                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);

                String nameDay;
                String ulrTwo;

                switch (i){
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read =  context.openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        StringBuilder namePred = new StringBuilder();
                        StringBuilder kab= new StringBuilder();
                        StringBuilder dz= new StringBuilder();
                        StringBuilder ocenka = new StringBuilder();
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("= =").append("=\n");
                            helpKab = help[1].split(",");


                            namePred.append(helpKab[0]).append("=");
                            kab.append(helpKab[1].substring(1)).append("=");
                            dz.append(" =");
                            ocenka.append(" =");
                        }

                        helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                    } catch (FileNotFoundException q) {
                        helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz), ""));
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write =  new FileOutputStream(FileTxt);
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
        final SharedPreferences.Editor editor = settings.edit();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
             viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
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
            dateNedel = view.findViewById(R.id.textViewDnew);
            String[] textUrl = new String[4];
            if(startNedeli < 10) textUrl[0] = "0" + startNedeli; else textUrl[0] = Integer.toString(startNedeli);
            if(startMes < 10) textUrl[1] = "0" + startMes; else textUrl[1] = Integer.toString(startMes);
            if(endNedeli < 10) textUrl[2] = "0" + endNedeli; else textUrl[2] = Integer.toString(endNedeli);
            if(endMes < 10) textUrl[3] = "0" + endMes; else textUrl[3] = Integer.toString(endMes);


                dateNedel.setText(textUrl[0] + "." + textUrl[1] + " - " + textUrl[2] + "." + textUrl[3]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startNedeli = settings.getInt("StartNedeli",1);
            nameMes = settings.getString("StartMes","Jan");
            startMes = 0;
            dayInMes = 0;
            endMes = 0;
            endNedeli = 0;
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
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);


                String nameDay;
                String ulrTwo;

                switch (i){
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileInputStream read =  new  FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    StringBuilder namePred = new StringBuilder();
                    StringBuilder kab = new StringBuilder();
                    StringBuilder dz = new StringBuilder();
                    StringBuilder ocenka = new StringBuilder();
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        namePred = new StringBuilder(helpKab[0] + "=");
                        kab = new StringBuilder(helpKab[1].substring(1) + "=");
                        if(2 <= help.length)
                            dz = new StringBuilder(help[1] + "=");
                        else
                            dz = new StringBuilder(" =");
                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        namePred.append(helpKab[0]).append("=");
                        kab.append(helpKab[1].substring(1)).append("=");
                        if (2 <= help.length)
                            dz.append(help[1]).append("=");
                        else
                            dz.append(" =");
                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read =  context.openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        StringBuilder namePred = new StringBuilder();
                        StringBuilder kab= new StringBuilder();
                        StringBuilder dz= new StringBuilder();
                        StringBuilder ocenka = new StringBuilder();
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("= =").append("=\n");
                            helpKab = help[1].split(",");


                            namePred.append(helpKab[0]).append("=");
                            kab.append(helpKab[1].substring(1)).append("=");
                            dz.append(" =");
                            ocenka.append(" =");
                        }

                        helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                        helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz), ""));
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write =  new FileOutputStream(FileTxt);
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
        final SharedPreferences.Editor editor = settings.edit();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
             viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
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
            dateNedel = view.findViewById(R.id.textViewDnew);
            String[] textUrl = new String[4];
            if(startNedeli < 10) textUrl[0] = "0" + startNedeli; else textUrl[0] = Integer.toString(startNedeli);
            if(startMes < 10) textUrl[1] = "0" + startMes; else textUrl[1] = Integer.toString(startMes);
            if(endNedeli < 10) textUrl[2] = "0" + endNedeli; else textUrl[2] = Integer.toString(endNedeli);
            if(endMes < 10) textUrl[3] = "0" + endMes; else textUrl[3] = Integer.toString(endMes);


                dateNedel.setText(textUrl[0] + "." + textUrl[1] + " - " + textUrl[2] + "." + textUrl[3]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startNedeli = settings.getInt("StartNedeli",1);
            nameMes = settings.getString("StartMes","Jan");
            startMes = 0;
            dayInMes = 0;
            endMes = 0;
            endNedeli = 0;
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
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;

                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);


                String nameDay;
                String ulrTwo;

                switch (i){
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileInputStream read =  new  FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    StringBuilder namePred = new StringBuilder();
                    StringBuilder kab = new StringBuilder();
                    StringBuilder dz = new StringBuilder();
                    StringBuilder ocenka = new StringBuilder();
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        namePred = new StringBuilder(helpKab[0] + "=");
                        kab = new StringBuilder(helpKab[1].substring(1) + "=");
                    if(2 <= help.length)
                        dz = new StringBuilder(help[1] + "=");
                    else
                        dz = new StringBuilder(" =");

                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        namePred.append(helpKab[0]).append("=");
                        kab.append(helpKab[1].substring(1)).append("=");
                        if (2 <= help.length)
                        dz.append(help[1]).append("=");
                        else
                            dz.append(" =");

                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read =  context.openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        StringBuilder namePred = new StringBuilder();
                        StringBuilder kab= new StringBuilder();
                        StringBuilder dz= new StringBuilder();
                        StringBuilder ocenka = new StringBuilder();
                        String[] help, helpKab;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            stringBuffer.append(help[1]).append("= =").append("=\n");
                            helpKab = help[1].split(",");


                            namePred.append(helpKab[0]).append("=");
                            kab.append(helpKab[1].substring(1)).append("=");
                            dz.append(" =");
                            ocenka.append(" =");
                        }
                        helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                        helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz), ""));
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write =  new FileOutputStream(FileTxt);
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
    class ClearOcenkiAsyncTask extends AsyncTask<Void,String[],Void>{
        final SharedPreferences.Editor editor = settings.edit();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
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
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;

                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);


                String nameDay;

                switch (i){
                    case 1:
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                StringBuilder stringBuffer = new StringBuilder();
                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                    File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    if (!FileTxt.exists()) {
                        FileTxt.createNewFile();
                    }

                    FileInputStream read =  new  FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read;
                    StringBuilder namePred = new StringBuilder();
                    StringBuilder kab = new StringBuilder();
                    StringBuilder dz = new StringBuilder();
                    StringBuilder ocenka = new StringBuilder();
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        namePred = new StringBuilder(helpKab[0] + "=");
                        kab = new StringBuilder(helpKab[1].substring(1) + "=");
                        if(2 <= help.length)
                            dz.append(help[1]).append("=");
                        else
                            dz.append(" =");
                        ocenka.append(" =");
                        stringBuffer.append(help[0]).append("=").append(help[1]).append("=\n");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        namePred.append(helpKab[0]).append("=");
                        kab.append(helpKab[1].substring(1)).append("=");
                        if(2 <= help.length)
                            dz.append(help[1]).append("=");
                        else
                            dz.append(" =");
                        ocenka.append(" =");
                        stringBuffer.append(help[0]).append("=").append(help[1]).append("=\n");
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(),dz.toString(), ocenka.toString()));

                } catch (FileNotFoundException e) {
                    helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz),""));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                    File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    if (!FileTxt.exists()) {
                        FileTxt.createNewFile();
                    }

                    FileOutputStream write =  new FileOutputStream(FileTxt);
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

    class ClearDzAsyncTask extends AsyncTask<Void,String[],Void>{
        final SharedPreferences.Editor editor = settings.edit();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
             viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
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
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;

                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);


                String nameDay;

                switch (i){
                    case 1:
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                StringBuilder stringBuffer = new StringBuilder();
                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileInputStream read =  new  FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read;
                    StringBuilder namePred = new StringBuilder();
                    StringBuilder kab = new StringBuilder();
                    StringBuilder dz = new StringBuilder();
                    StringBuilder ocenka = new StringBuilder();
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        namePred = new StringBuilder(helpKab[0] + "=");
                        kab = new StringBuilder(helpKab[1].substring(1) + "=");
                        dz.append(" =");
                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                        stringBuffer.append(help[0]).append("=\n");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        namePred.append(helpKab[0]).append("=");
                        kab.append(helpKab[1].substring(1)).append("=");
                        dz.append(" =");
                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                        stringBuffer.append(help[0]).append("= =").append(help[2]).append("=\n");
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));

                } catch (FileNotFoundException e) {
                    helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz),""));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write =  new FileOutputStream(FileTxt);
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
        final SharedPreferences.Editor editor = settings.edit();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
             viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
            viewPager.setPageMargin(6);
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
            dateNedel = view.findViewById(R.id.textViewDnew);
            String[] textUrl = new String[4];
            if(startNedeli < 10) textUrl[0] = "0" + startNedeli; else textUrl[0] = Integer.toString(startNedeli);
            if(startMes < 10) textUrl[1] = "0" + startMes; else textUrl[1] = Integer.toString(startMes);
            if(endNedeli < 10) textUrl[2] = "0" + endNedeli; else textUrl[2] = Integer.toString(endNedeli);
            if(endMes < 10) textUrl[3] = "0" + endMes; else textUrl[3] = Integer.toString(endMes);


                dateNedel.setText(textUrl[0] + "." + textUrl[1] + " - " + textUrl[2] + "." + textUrl[3]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Date date = new Date();
            startNedeli = Integer.parseInt(date.toString().substring(8,10));
            nameMes = date.toString().substring(4,7);
            startMes = 0;
            dayInMes = 0;
            endMes = 0;
            String dayName = date.toString().substring(0, 3);
            endNedeli = 0;
            int dayRemove = 0;
            switch (nameMes){
                case "Jan":
                    dayInMes = new Const().Jan;
                    startMes = 1;
                    endMes = 2;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Feb":
                    dayInMes = new Const().Feb;
                    startMes = 2;
                    endMes = 3;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Mar":
                    dayInMes = new Const().Mar;
                    startMes = 3;
                    endMes = 4;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Apr":
                    dayInMes = new Const().Apr;
                    startMes = 4;
                    endMes = 5;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "May":
                    dayInMes = new Const().May;
                    startMes = 5;
                    endMes = 6;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Jun":
                    dayInMes = new Const().Jun;
                    startMes = 6;
                    endMes = 7;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Jul":
                    dayInMes = new Const().Jul;
                    startMes = 7;
                    endMes = 8;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Aug":
                    dayInMes = new Const().Aug;
                    startMes = 8;
                    endMes = 9;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Sep":
                    dayInMes = new Const().Sep;
                    startMes = 9;
                    endMes = 10;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Oct":
                    dayInMes = new Const().Oct;
                    startMes = 10;
                    endMes = 11;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Nov":
                    dayInMes = new Const().Nov;
                    startMes = 11;
                    endMes = 12;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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
                case "Dec":
                    dayInMes = new Const().Dec;
                    startMes = 12;
                    endMes = 1;
                    switch (dayName){
                        case "Tue":
                            dayRemove = 1;
                            break;
                        case "Wed":
                            dayRemove = 2;
                            break;
                        case "Thu":
                            dayRemove = 3;
                            break;
                        case "Fri":
                            dayRemove = 4;
                            break;
                        case "Sat":
                            dayRemove = 5;
                            break;
                        case "Sun":
                            dayRemove = 6;
                            break;
                    }

                    startNedeli = startNedeli - dayRemove;
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

            editor.putInt("StartNedeli",startNedeli);
            editor.putString("StartMes",nameMes);
            editor.putInt("IntMes",startMes);
            editor.putInt("Year",date.getYear());
            editor.apply();
            for (int i = 0; i < 6; i++){
                if(i + 1 == 6 && !settings.getBoolean("SaturdaySettings",true))
                    continue;

                String url = (startNedeli + i) + "." + startMes + "." + date.getYear();

                String nameDay ;
                String ulrTwo ;

                switch (i){
                    case 1:
                        ulrTwo = "Tuesday.txt";
                        nameDay = context.getString(R.string.tuesday);
                        break;
                    case 2:
                        ulrTwo = "Wednesday.txt";
                        nameDay = context.getString(R.string.wednesday);
                        break;
                    case 3:
                        ulrTwo = "Thursday.txt";
                        nameDay = context.getString(R.string.thursday);
                        break;
                    case 4:
                        ulrTwo = "Friday.txt";
                        nameDay = context.getString(R.string.friday);
                        break;
                    case 5:
                        ulrTwo = "Saturday.txt";
                        nameDay = context.getString(R.string.saturday);
                        break;

                    default:
                        ulrTwo = "Monday.txt";
                        nameDay = context.getString(R.string.monday);
                        break;
                }
                try {
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileInputStream read =  new  FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    StringBuilder namePred = new StringBuilder();
                    StringBuilder kab = new StringBuilder();
                    StringBuilder dz = new StringBuilder();
                    StringBuilder ocenka = new StringBuilder();
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        namePred = new StringBuilder(helpKab[0] + "=");
                        kab = new StringBuilder(helpKab[1].substring(1) + "=");
                        if(2 <= help.length)
                            dz = new StringBuilder(help[1] + "=");
                        else
                            dz = new StringBuilder(" =");
                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        namePred.append(helpKab[0]).append("=");
                        kab.append(helpKab[1].substring(1)).append("=");
                        if (2 <= help.length)
                            dz.append(help[1]).append("=");
                        else
                            dz.append(" =");

                        if (3 <= help.length)
                            ocenka.append(help[2]).append("=");
                        else
                            ocenka.append(" =");
                    }

                    helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read =  context.openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        StringBuilder namePred = new StringBuilder();
                        StringBuilder kab= new StringBuilder();
                        StringBuilder dz= new StringBuilder();
                        StringBuilder ocenka = new StringBuilder();
                        String[] help, helpKab;
                        String delimeter = "=";
                            if((temp_read = bufferedReader.readLine()) == null)
                                throw new FileNotFoundException();
                            else{
                                help = temp_read.split(delimeter);
                                stringBuffer.append(help[1]).append("= =").append("=\n");
                                helpKab = help[1].split(",");


                                namePred.append(helpKab[0]).append("=");
                                kab.append(helpKab[1].substring(1)).append("=");
                                dz.append(" =");
                                ocenka.append(" =");
                            }
                            while ((temp_read = bufferedReader.readLine()) != null) {
                                help = temp_read.split(delimeter);
                                stringBuffer.append(help[1]).append("= =").append("=\n");
                                helpKab = help[1].split(",");


                                namePred.append(helpKab[0]).append("=");
                                kab.append(helpKab[1].substring(1)).append("=");
                                dz.append(" =");
                                ocenka.append(" =");
                            }
                            helperDnewniks.add(new helperDnewnik(nameDay, namePred.toString(), kab.toString(), dz.toString(), ocenka.toString()));
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                        helperDnewniks.add(new helperDnewnik(nameDay,getString(R.string.nullTimetablesName), getString(R.string.nullTimetablesKab), getString(R.string.nullTimetablesDz),""));
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write =  new FileOutputStream(FileTxt);
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
                }catch (NullPointerException wq){
                }

            }
            return null;
        }



    }

    public void notifyTab(){
        viewPager.setPadding(settings.getInt("dpSizeSettings",120), 0, settings.getInt("dpSizeSettings",120), 0);
    }

}