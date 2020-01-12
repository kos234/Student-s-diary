package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class OcenkiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String url;
    private TextView textViewDate;
    LayoutInflater inflater;
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflaterFrag, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflaterFrag.inflate(R.layout.fragment_ocenki, container, false);
        inflater = LayoutInflater.from(context);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar5);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() != R.id.Ocenki)
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        editor = settings.edit();
        linearLayout = view.findViewById(R.id.LinerOcenki_Item);
        textViewDate = view.findViewById(R.id.textViewOcen);
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                    for(int i = 0; i < ConfirmationValue.length; i++)
                        if(!ConfirmationValue[i].equals(getString(R.string.Not_Confirmed)))
                            throw new Povtor("KRIA", 1);
                        new ClearAsyncTask().execute();
                    Toast.makeText(context,"Мона",Toast.LENGTH_LONG).show();
            }catch (Povtor povtor){
                    Toast.makeText(context,"Низя",Toast.LENGTH_LONG).show();
                }
            }
        });

        if(settings.getBoolean("FirstStartOcenki", true)){
            final LayoutInflater li = LayoutInflater.from(getActivity());
            final View promptsView = li.inflate(R.layout.mes_ocenki , null);
            AlertDialog.Builder mesSetting = new AlertDialog.Builder(getActivity());
            mesSetting.setView(promptsView);
            CalendarView calendarView = promptsView.findViewById(R.id.calendar_view);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                   editor.putInt("mesStartOcenki",i1);
                   editor.apply();
                }
            });
            mesSetting
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.save),
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id) {
                                    AlertDialog.Builder warning = new AlertDialog.Builder(getActivity());
                                    warning.setMessage(context.getString(R.string.warningOcenki)).setCancelable(true).setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                            editor.putBoolean("FirstStartOcenki",false);
                                            editor.apply();
                                            new StartAsyncTask().execute();
                                        }
                                    });
                                    AlertDialog alertDialog = warning.create();
                                    alertDialog.setTitle(context.getString(R.string.Warning));
                                    alertDialog.show();
                                }

                            });

            //Создаем AlertDialog:
            AlertDialog alertDialog = mesSetting.create();

            //и отображаем его:

            alertDialog.show();
        }else
            new StartAsyncTask().execute();

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


    public TableRow Confirmation(){
        TableRow Confirmation = (TableRow) inflater.inflate(R.layout.confirmed_ocenki, null);
        String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
        int[] ConfirmationID = new int[]{R.id.one, R.id.two, R.id.three, R.id.four, R.id.year, R.id.examination, R.id.end};

        for(int i = 0; i < 7; i++){
            if(!ConfirmationValue[i].equals(getString(R.string.Not_Confirmed))){
                TextView ConfirmationTextView = Confirmation.findViewById(ConfirmationID[i]);
                ConfirmationTextView.setText(getString(R.string.Confirmed));
            }
        }
        return Confirmation;
    }

    public TableRow CreateRow(String[] strings, int NumString){

        TableRow Yrok = (TableRow) inflater.inflate(R.layout.ocenki_item, null);
        TextView  textName = Yrok.findViewById(R.id.nameYrokOcenki);
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


    public class ClearAsyncTask extends AsyncTask<Void,TableRow,Void>{

        TableLayout tableLayout = new TableLayout(context);


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
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
        }

        @Override
        protected void onProgressUpdate(TableRow... values) {
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList predmeti = new ArrayList();
            String[] day = getResources().getStringArray(R.array.DayTxt);
            StringBuffer stringBuffer = new StringBuffer();
            String writeString;
            for (int k = 0; k < day.length; k++) {

                try {
                    FileInputStream read = getActivity().openFileInput(day[k]);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    String[] help,helpName;
                    String delimeter = "=";
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);
                        helpName = help[1].split(",");

                        if(predmeti.indexOf(helpName[0]) < 0){
                            predmeti.add(helpName[0]);
                            writeString = helpName[0] + "= = = = = = = ";
                            stringBuffer.append(writeString).append("\n");
                            publishProgress(CreateRow(writeString.split(delimeter),predmeti.size()));
                        }
                    }
                } catch (FileNotFoundException q) {
                    q.printStackTrace();
                } catch (IOException j) {
                    j.printStackTrace();
                }

            }

            predmeti.add(getString(R.string.behavior));
            writeString = getString(R.string.behavior) + "= = = = = = = ";
            stringBuffer.append(writeString).append("\n");
            publishProgress(CreateRow(writeString.split("="),predmeti.size()));

            TableRow Confirmation = (TableRow) inflater.inflate(R.layout.confirmed_ocenki, null);
            publishProgress(Confirmation);



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
            return null;
        }
    }

    public class StartAsyncTask extends AsyncTask<Void,TableRow,Void>{

        TableLayout tableLayout = new TableLayout(context);

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
            linearLayout.removeAllViews();
            tableLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(tableLayout);
            textViewDate.setText(url);
        }

        @Override
        protected void onProgressUpdate(TableRow... values) {
            super.onProgressUpdate(values);
            tableLayout.addView(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TableRow Bar = (TableRow) inflater.inflate(R.layout.bar_ocenki, null);
            publishProgress(Bar);
            Date date = new Date();
            if(settings.getInt("mesStartOcenki",8) <= date.getMonth()) {
                url = (date.getYear() + 1900) + " - " + (date.getYear() + 1901);
                editor.putInt("endUrl", date.getYear() + 1901);
            }else {
                url = (date.getYear() + 1899) + " - " + (date.getYear() + 1900);
                editor.putInt("endUrl", date.getYear() + 1900);

            }

            editor.apply();
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
                }

                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    predmeti.add(help[0]);
                    publishProgress(CreateRow(help,predmeti.size()));
                }

                publishProgress(Confirmation());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                String[] day = getResources().getStringArray(R.array.DayTxt);
                StringBuffer stringBuffer = new StringBuffer();
                String writeString;
                for (int k = 0; k < day.length; k++) {

                    try {
                        FileInputStream read = getActivity().openFileInput(day[k]);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help,helpName;
                        String delimeter = "=";
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split(delimeter);
                            helpName = help[1].split(",");

                            if(predmeti.indexOf(helpName[0]) < 0){
                               predmeti.add(helpName[0]);
                               writeString = helpName[0] + "= = = = = = = ";
                               stringBuffer.append(writeString).append("\n");
                               publishProgress(CreateRow(writeString.split(delimeter),predmeti.size()));
                            }
                        }
                    } catch (FileNotFoundException q) {
                        q.printStackTrace();
                    } catch (IOException j) {
                        j.printStackTrace();
                    }

                }

                predmeti.add(getString(R.string.behavior));
                writeString = getString(R.string.behavior) + "= = = = = = = ";
                stringBuffer.append(writeString).append("\n");
                publishProgress(CreateRow(writeString.split("="),predmeti.size()));

                TableRow Confirmation = (TableRow) inflater.inflate(R.layout.confirmed_ocenki, null);
                publishProgress(Confirmation);



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

            return null;
        }


    }


}
