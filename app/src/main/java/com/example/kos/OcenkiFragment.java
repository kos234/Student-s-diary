package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class OcenkiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings,
                            Confirmed,
                            Current_Theme;
    private SharedPreferences.Editor editor;
    private String url;
    private TextView textViewDate;
    private LayoutInflater inflater;
    private LinearLayout linearLayout;


    @Override
    public View onCreateView(LayoutInflater inflaterFrag, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflaterFrag.inflate(R.layout.fragment_ocenki, container, false);
        try {
        inflater = LayoutInflater.from(context);
        settings = Objects.requireNonNull(getActivity()).getSharedPreferences("Settings", MODE_PRIVATE);
        Confirmed = getActivity().getSharedPreferences("Confirmed", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        editor = settings.edit();
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar5);
        Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
        menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(menuToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ((MainActivity) Objects.requireNonNull(getActivity())).openDrawer();
                }catch (Exception error){((MainActivity) context).errorStack(error);}
            }
        });
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
        linearLayout = view.findViewById(R.id.LinerOcenki_Item);
        textViewDate = view.findViewById(R.id.textViewOcen);
        textViewDate.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);

                AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setView(promptsView);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);
                final AlertDialog alertDialog = deleted.create();

                TextView textTitle = promptsView.findViewById(R.id.title_alert);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                textTitle.setText(context.getString(R.string.deleting));

                TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textBottomTitle.setText(context.getString(R.string.ReplaceOcenki));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setText(getString(R.string.cancel));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.hide();
                    }
                });

                TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{
                            String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                            for (String s : ConfirmationValue)
                                if (!s.equals(getString(R.string.Not_Confirmed)))
                                    throw new Povtor("KRIA");
                            new ClearAsyncTask().execute();
                        }catch (Povtor povtor){
                            Toast.makeText(context,getString(R.string.you_have_already_confirmed_grades),Toast.LENGTH_LONG).show();
                        }

                        alertDialog.hide();
                    }
                });
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonSave.setText(getString(R.string.yes));

                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();


                }catch (Exception error){((MainActivity) context).errorStack(error);}}
        });
        if(!settings.getBoolean("FirstStartOcenki", true))
            new StartAsyncTask().execute();

        ImageButton imageButtonLeft = view.findViewById(R.id.imageButtonLeft);
        imageButtonLeft.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
        imageButtonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                    new LeftAsyncTask().execute();
                    }catch (Exception error){((MainActivity) context).errorStack(error);}
                }
            });
        ImageButton imageButtonRight = view.findViewById(R.id.imageButtonRight);
        imageButtonRight.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
        imageButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {try{
                new RightAsyncTask().execute();
            }catch (Exception error){((MainActivity) context).errorStack(error);}
            }
        });

        }catch (Exception error){((MainActivity) context).errorStack(error);}

        return view;
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

    public void cheakStart (){
        if(settings.getBoolean("FirstStartOcenki", true)){

            final LayoutInflater li = LayoutInflater.from(getActivity());
            final View promptsView = li.inflate(R.layout.mes_ocenki , null);

            AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
            deleted.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.liner_mes_ocenki).setBackground(alertbackground);
            final AlertDialog alertDialog = deleted.create();

            TextView textTitle = promptsView.findViewById(R.id.title_mes_ocenki);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            textTitle = promptsView.findViewById(R.id.bottom_title_mes_ocenki);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            CalendarView calendarView = promptsView.findViewById(R.id.calendar_view);
            calendarView.setWeekNumberColor(Color.BLUE);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    try{
                    editor.putInt("mesStartOcenki",i1);
                    editor.apply();
                    }catch (Exception error){((MainActivity) context).errorStack(error);}}
            });

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
            ButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {try{
                    alertDialog.hide();
                }catch (Exception error){((MainActivity) context).errorStack(error);}}
            });

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {try{
                    editor.putBoolean("FirstStartOcenki",false);
                    editor.apply();
                    new StartAsyncTask().execute();
                    alertDialog.hide();

                    final LayoutInflater liW = LayoutInflater.from(getActivity());
                    final View promptsViewWarning = liW.inflate(R.layout.alert_delete_dnewnik , null);

                    AlertDialog.Builder warning = new AlertDialog.Builder(getActivity());
                    warning.setView(promptsViewWarning);
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                    Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if(settings.getBoolean("BorderAlertSettings",false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                    promptsViewWarning.findViewById(R.id.alert_delete).setBackground(alertbackground);
                    final AlertDialog alertDialogWarning = warning.create();

                    TextView textTitle = promptsViewWarning.findViewById(R.id.title_alert);
                    textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                    textTitle.setText(context.getString(R.string.Warning));

                    TextView textBottomTitle = promptsViewWarning.findViewById(R.id.title_bottom_alert);
                    textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    textBottomTitle.setText(context.getString(R.string.warningOcenki));

                    promptsViewWarning.findViewById(R.id.button_one_alert).setVisibility(View.INVISIBLE);


                    TextView ButtonSave = promptsViewWarning.findViewById(R.id.button_two_alert);
                    ButtonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialogWarning.hide();
                        }
                    });
                    ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                    ButtonSave.setText(getString(R.string.Ok));

                    Objects.requireNonNull(alertDialogWarning.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialogWarning.show();
                }catch (Exception error){((MainActivity) context).errorStack(error);}}
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

        }
    }

    private TableRow Confirmation(String urlConf){
        TableRow Confirmation = CreateStaticBar(R.layout.confirmed_ocenki);
        String[] ConfirmationValue = Confirmed.getString(urlConf,getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
        int[] ConfirmationID = new int[]{R.id.one, R.id.two, R.id.three, R.id.four, R.id.year, R.id.examination, R.id.end};

        for(int i = 0; i < 7; i++){
            if(!ConfirmationValue[i].equals(getString(R.string.Not_Confirmed))){
                TextView ConfirmationTextView = Confirmation.findViewById(ConfirmationID[i]);
                ConfirmationTextView.setText(getString(R.string.Confirmed));
            }
        }
        return Confirmation;
    }

    private TableRow CreateStaticBar(int layout_id){
        TableRow bar = (TableRow) inflater.inflate(layout_id, null);
        bar.setBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));

        TextView  textName = bar.findViewById(R.id.title_bar);
        TextView textOne = bar.findViewById(R.id.one);
        TextView textTwo = bar.findViewById(R.id.two);
        TextView textThree = bar.findViewById(R.id.three);
        TextView textFour = bar.findViewById(R.id.four);
        TextView textYear = bar.findViewById(R.id.year);
        TextView  textExamination = bar.findViewById(R.id.examination);
        TextView  textEnd = bar.findViewById(R.id.end);

        textName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textOne.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textTwo.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textThree.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textFour.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textYear.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textExamination.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textEnd.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

        textName.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textOne.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textTwo.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textThree.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textFour.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textYear.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textExamination.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textEnd.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));

        return bar;
    }

    private TableRow CreateRow(String[] strings, int NumString){

        TableRow Yrok = (TableRow) inflater.inflate(R.layout.ocenki_item, null);
        Yrok.setBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));
        Yrok.setId(10203040 + NumString);

        TextView textName = Yrok.findViewById(R.id.nameYrokOcenki);
        TextView textOne = Yrok.findViewById(R.id.ocenka_one);
        TextView textTwo = Yrok.findViewById(R.id.ocenka_two);
        TextView textThree = Yrok.findViewById(R.id.ocenka_three);
        TextView textFour = Yrok.findViewById(R.id.ocenka_four);
        TextView textYear = Yrok.findViewById(R.id.ocenka_year);
        TextView  textExamination = Yrok.findViewById(R.id.ocenka_examination);
        TextView  textEnd = Yrok.findViewById(R.id.ocenka_end);

        textName.setText(strings[0]);
        textOne.setText(strings[1]);
        textTwo.setText(strings[2]);
        textThree.setText(strings[3]);
        textFour.setText(strings[4]);
        textYear.setText(strings[5]);
        textExamination.setText(strings[6]);
        textEnd.setText(strings[7]);

        textName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        textOne.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textTwo.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textThree.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textFour.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textYear.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textExamination.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textEnd.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

        textName.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textOne.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textTwo.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textThree.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textFour.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textYear.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textExamination.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        textEnd.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));



        TextView numStolbik = Yrok.findViewById(R.id.numStolb_1);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_2);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_3);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_4);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_5);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_6);
        numStolbik.setText(Integer.toString(NumString));
        numStolbik = Yrok.findViewById(R.id.numStolb_7);
        numStolbik.setText(Integer.toString(NumString));
        return Yrok;
    }

    class RightAsyncTask extends AsyncTask<Void,TableRow,Void>{
        final ArrayList<String[]> TabArray = new ArrayList<>();
        final TableLayout tableLayout = new TableLayout(context);

        @Override
        protected void onPreExecute() {try{
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }catch (Exception error){((MainActivity) context).errorStack(error);} }

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
            new CheakWriteInStolb().execute(new ConstrCheak(TabArray,tableLayout));
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onProgressUpdate(TableRow... values) {try{
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }catch (Exception error){((MainActivity) context).errorStack(error);} }

        @Override
        protected Void doInBackground(Void... voids) {try{
            publishProgress(CreateStaticBar(R.layout.bar_ocenki));
                url = settings.getInt("endUrl",2020) + " - " + (settings.getInt("endUrl",2020) + 1);
                editor.putInt("endUrl", (settings.getInt("endUrl",2020) + 1));


            ArrayList predmeti = new ArrayList();

            try {
                File mFolder = new File(context.getFilesDir() + "/ocenki");
                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!FileTxt.exists()) {
                    FileTxt.createNewFile();
                }

                FileInputStream read =  new FileInputStream(FileTxt);
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String temp_read;
                String[] help;
                String delimeter = "=";

                if((temp_read = bufferedReader.readLine()) == null){
                    throw new FileNotFoundException();
                }else{
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                publishProgress(Confirmation(url));
                editor.putInt("PredmetiSize", predmeti.size());
                editor.apply();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                String[] day = getResources().getStringArray(R.array.DayTxt);
                StringBuilder stringBuffer = new StringBuilder();
                String writeString;
                for (String s : day) {

                    try {
                        FileInputStream read = getActivity().openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help, helpName;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            helpName = help[1].split(",");

                            if (predmeti.indexOf(helpName[0]) < 0) {
                                predmeti.add(helpName[0]);
                                writeString = helpName[0] + "= = = = = = = ";
                                stringBuffer.append(writeString).append("\n");
                                publishProgress(CreateRow(writeString.split(delimeter), predmeti.size()));
                            }
                        }
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }

                }

                if(settings.getBoolean("PovedSettings",true)){
                    predmeti.add(getString(R.string.behavior));
                    writeString = getString(R.string.behavior) + "= = = = = = = ";
                    stringBuffer.append(writeString).append("\n");
                    publishProgress(CreateRow(writeString.split("="),predmeti.size()));
                }

                publishProgress(CreateStaticBar(R.layout.confirmed_ocenki));


                if(stringBuffer != null)
                try {
                    File mFolder = new File(context.getFilesDir() + "/ocenki");
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

        }catch (Exception error){((MainActivity) context).errorStack(error);} return null;
        }


    }

    class LeftAsyncTask extends AsyncTask<Void,TableRow,Void>{
        final ArrayList<String[]> TabArray = new ArrayList<>();
        final TableLayout tableLayout = new TableLayout(context);

        @Override
        protected void onPreExecute() {try{
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
            new CheakWriteInStolb().execute(new ConstrCheak(TabArray,tableLayout));
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onProgressUpdate(TableRow... values) {try{
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected Void doInBackground(Void... voids) {try{
            publishProgress(CreateStaticBar(R.layout.bar_ocenki));
            url = (settings.getInt("endUrl",2020) - 2) + " - " + (settings.getInt("endUrl",2020) - 1);
            editor.putInt("endUrl", (settings.getInt("endUrl",2020) - 1));


            ArrayList predmeti = new ArrayList();

            try {
                File mFolder = new File(context.getFilesDir() + "/ocenki");
                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!FileTxt.exists()) {
                    FileTxt.createNewFile();
                }

                FileInputStream read =  new FileInputStream(FileTxt);
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String temp_read;
                String[] help;
                String delimeter = "=";

                if((temp_read = bufferedReader.readLine()) == null){
                    throw new FileNotFoundException();
                }else{
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                publishProgress(Confirmation(url));
                editor.putInt("PredmetiSize", predmeti.size());
                editor.apply();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                String[] day = getResources().getStringArray(R.array.DayTxt);
                StringBuilder stringBuffer = new StringBuilder();
                String writeString;
                for (String s : day) {

                    try {
                        FileInputStream read = getActivity().openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help, helpName;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            helpName = help[1].split(",");

                            if (predmeti.indexOf(helpName[0]) < 0) {
                                predmeti.add(helpName[0]);
                                writeString = helpName[0] + "= = = = = = = ";
                                stringBuffer.append(writeString).append("\n");
                                publishProgress(CreateRow(writeString.split(delimeter), predmeti.size()));
                            }
                        }
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }

                }

                if(settings.getBoolean("PovedSettings",true)){
                    predmeti.add(getString(R.string.behavior));
                    writeString = getString(R.string.behavior) + "= = = = = = = ";
                    stringBuffer.append(writeString).append("\n");
                    publishProgress(CreateRow(writeString.split("="),predmeti.size()));
                }

                publishProgress(CreateStaticBar(R.layout.confirmed_ocenki));


                if(stringBuffer != null)
                try {
                    File mFolder = new File(context.getFilesDir() + "/ocenki");
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

        }catch (Exception error){((MainActivity) context).errorStack(error);}return null;
        }


    }

    class ClearAsyncTask extends AsyncTask<Void,TableRow,Void>{

        final TableLayout tableLayout = new TableLayout(context);


        @Override
        protected void onPreExecute() {try{
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onProgressUpdate(TableRow... values) {try{
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected Void doInBackground(Void... voids) {try{
            publishProgress(CreateStaticBar(R.layout.bar_ocenki));
            ArrayList predmeti = new ArrayList();
            String[] day = getResources().getStringArray(R.array.DayTxt);
            StringBuilder stringBuffer = new StringBuilder();
            String writeString;
            for (String s : day) {

                try {
                    FileInputStream read = getActivity().openFileInput(s);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    String[] help, helpName;
                    String delimeter = "=";
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);
                        helpName = help[1].split(",");

                        if (predmeti.indexOf(helpName[0]) < 0) {
                            predmeti.add(helpName[0]);
                            writeString = helpName[0] + "= = = = = = = ";
                            stringBuffer.append(writeString).append("\n");
                            publishProgress(CreateRow(writeString.split(delimeter), predmeti.size()));
                        }
                    }
                } catch (FileNotFoundException q) {
                    q.printStackTrace();
                } catch (IOException j) {
                    j.printStackTrace();
                }

            }

            if(settings.getBoolean("PovedSettings",true)){
                predmeti.add(getString(R.string.behavior));
                writeString = getString(R.string.behavior) + "= = = = = = = ";
                stringBuffer.append(writeString).append("\n");
                publishProgress(CreateRow(writeString.split("="),predmeti.size()));
            }

            publishProgress(CreateStaticBar(R.layout.confirmed_ocenki));


            if(stringBuffer != null)
            try {
                File mFolder = new File(context.getFilesDir() + "/ocenki");
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
        }catch (Exception error){((MainActivity) context).errorStack(error);} return null;
        }
    }
    class CheakWriteInStolb extends AsyncTask<ConstrCheak,CheakConvenorResult,Void> {

        @Override
        protected void onProgressUpdate(CheakConvenorResult... values) {
            super.onProgressUpdate(values);
            try{
            int FrameId;

            switch (values[0].getStolbID()) {
                case 2:
                    FrameId = R.id.frame_ocenki_two;
                    break;

                case 3:
                    FrameId = R.id.frame_ocenki_three;
                    break;

                case 4:
                    FrameId = R.id.frame_ocenki_four;
                    break;

                case 5:
                    FrameId = R.id.frame_ocenki_year;
                    break;

                case 6:
                    FrameId = R.id.frame_ocenki_examination;
                    break;

                case 7:
                    FrameId = R.id.frame_ocenki_end;
                    break;
                    default: FrameId = R.id.frame_ocenki_one; break;
            }

            FrameLayout frameLayout;
            frameLayout = values[0].getTableRow().findViewById(FrameId);
            frameLayout.setBackgroundColor(Color.RED);
            }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected Void doInBackground(ConstrCheak... value) {try{
            for (int i = 1; i < 8; i++) {
                for (int j = 0; j < value[0].getArrayList().size(); j++)
                    try {
                        String[] helpCheak = value[0].getArrayList().get(j),
                                ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                        if (!helpCheak[i].equals(" ") && ConfirmationValue[i-1].equals(getString(R.string.Not_Confirmed)))
                            throw new Povtor("KRIA");
                    } catch (Povtor povtor) {
                        TableRow tableRow;

                        tableRow = value[0].getTableLayout().findViewById(R.id.barOcenki);

                        publishProgress(new CheakConvenorResult(tableRow,i));
                        for (int l = 1; l <= settings.getInt("PredmetiSize", 2); l++) {
                            tableRow = value[0].getTableLayout().findViewById(10203040 + l);
                            publishProgress(new CheakConvenorResult(tableRow,i));
                        }
                        tableRow = value[0].getTableLayout().findViewById(R.id.confirmationBar);
                        publishProgress(new CheakConvenorResult(tableRow,i));
                    }
            }
        }catch (Exception error){((MainActivity) context).errorStack(error);}
            return null;
        }
    }

    class StartAsyncTask extends AsyncTask<Void,TableRow,Void>{
        final ArrayList<String[]> TabArray = new ArrayList<>();
        final TableLayout tableLayout = new TableLayout(context);

        @Override
        protected void onPreExecute() {try{
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);

            linearLayout.addView(progressBar, layoutParams);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
            new CheakWriteInStolb().execute(new ConstrCheak(TabArray, tableLayout));
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected void onProgressUpdate(TableRow... values) {try{
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected Void doInBackground(Void... voids) {try{
            publishProgress(CreateStaticBar(R.layout.bar_ocenki));
            Date date = new Date();
            if(settings.getInt("mesStartOcenki",8) <= date.getMonth()) {
                url = (date.getYear() + 1900) + " - " + (date.getYear() + 1901);
                editor.putInt("endUrl", date.getYear() + 1901);
            }else {
                url = (date.getYear() + 1899) + " - " + (date.getYear() + 1900);
                editor.putInt("endUrl", date.getYear() + 1900);

            }


            ArrayList predmeti = new ArrayList();

            try {
                File mFolder = new File(context.getFilesDir() + "/ocenki");
                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!FileTxt.exists()) {
                    FileTxt.createNewFile();
                }

                FileInputStream read =  new FileInputStream(FileTxt);
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String temp_read;
                String[] help;
                String delimeter = "=";

                if((temp_read = bufferedReader.readLine()) == null){
                    throw new FileNotFoundException();
                }else{
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                    TabArray.add(help);
                }

                publishProgress(Confirmation(url));
                editor.putInt("PredmetiSize", predmeti.size());
                editor.apply();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                String[] day = getResources().getStringArray(R.array.DayTxt);
                StringBuilder stringBuffer = new StringBuilder();
                String writeString;
                for (String s : day) {

                    try {
                        FileInputStream read = getActivity().openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help, helpName;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            helpName = help[1].split(",");

                            if (predmeti.indexOf(helpName[0]) < 0) {
                                predmeti.add(helpName[0]);
                                writeString = helpName[0] + "= = = = = = = ";
                                stringBuffer.append(writeString).append("\n");
                                publishProgress(CreateRow(writeString.split(delimeter), predmeti.size()));
                            }
                        }
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }

                }

                if(settings.getBoolean("PovedSettings",true)){
                    predmeti.add(getString(R.string.behavior));
                    writeString = getString(R.string.behavior) + "= = = = = = = ";
                    stringBuffer.append(writeString).append("\n");
                    publishProgress(CreateRow(writeString.split("="),predmeti.size()));
                }

                publishProgress(CreateStaticBar(R.layout.confirmed_ocenki));


                if(stringBuffer != null)
                try {
                    File mFolder = new File(context.getFilesDir() + "/ocenki");
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
        }catch (Exception error){((MainActivity) context).errorStack(error);}
            return null;
        }
    }
}