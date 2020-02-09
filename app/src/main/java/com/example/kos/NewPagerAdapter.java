package com.example.kos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NewPagerAdapter extends PagerAdapter {
    Context context;
    private String[] name;
    SharedPreferences settings,Current_Theme;
    List<ConstrFragmentViewPager> constrFragmentViewPagerArrayList;
    public FloatingActionButton floatingActionButton;

    public NewPagerAdapter(List<ConstrFragmentViewPager> constrFragmentViewPagerArrayList, Context context,FloatingActionButton floatingActionButton) {
        this.constrFragmentViewPagerArrayList = constrFragmentViewPagerArrayList;
        this.context = context;
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        if(settings.getBoolean("SaturdaySettings",true))
        name = new String[] {
                context.getString(R.string.monday),
                context.getString(R.string.tuesday),
                context.getString(R.string.wednesday),
                context.getString(R.string.thursday),
                context.getString(R.string.friday),
                context.getString(R.string.saturday)
        };
        else
            name = new String[] {
                    context.getString(R.string.monday),
                    context.getString(R.string.tuesday),
                    context.getString(R.string.wednesday),
                    context.getString(R.string.thursday),
                    context.getString(R.string.friday),
            };

        this.floatingActionButton = floatingActionButton;
    }

    @Override
    public int getCount() {
        return constrFragmentViewPagerArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return name[position];
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.fragment_item_pager, container, false);
        constrFragmentViewPagerArrayList.get(position).setView(view);
        final ArrayList<ConstrRecyclerView> product = constrFragmentViewPagerArrayList.get(position).getArray();
        RecyclerView recyclerView = view.findViewById(R.id.Zvonki_Recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final RecyclerAdapter adapter = constrFragmentViewPagerArrayList.get(position).getRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        TextView textView = view.findViewById(R.id.nullZvon);
        if(product.size() != 0)
            textView.setVisibility(View.INVISIBLE);
        else
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        final SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        final String url = constrFragmentViewPagerArrayList.get(position).getUrl();

        adapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {

                final LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setView(promptsView);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

                final AlertDialog Deleted = Delete.create();

                TextView textTitle = promptsView.findViewById(R.id.title_alert);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                textTitle.setText(context.getString(R.string.deleting));

                TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textBottomTitle.setText(context.getString(R.string.deleteLesson));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setText(context.getString(R.string.cancel));
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Deleted.hide();
                    }
                });

                TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringBuffer stringBuffer = new StringBuffer();
                        final String textTime = product.get(position).getTextName();
                        try {
                            FileInputStream read = context.openFileInput(url);
                            InputStreamReader reader = new InputStreamReader(read);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            String temp_read;
                            String[] help ;
                            String delimeter = "=";
                            while ((temp_read = bufferedReader.readLine()) != null) {

                                help = temp_read.split(delimeter);


                                if  (!help[0].equals(textTime))
                                    stringBuffer.append(temp_read).append("\n");
                            }

                            bufferedReader.close();
                            reader.close();
                            read.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        try {
                            FileOutputStream write = context.openFileOutput(url,context.MODE_PRIVATE);

                            write.write(stringBuffer.toString().getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        product.remove(position);
                        if(settings.getBoolean("AnimationSettings",true))
                            adapter.notifyItemRemoved(position);
                        else
                            adapter.notifyDataSetChanged();
                        if(product.size() == 0)
                            TextViewVisible(view);

                        Deleted.hide();

                    }
                });
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

                Deleted.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Deleted.show();

            }
        });

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                String textTime = product.get(position).getTextName();
                String textBottom = product.get(position).getTextBottom();

                final LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt , null);
                final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                newzvonok.setView(promptsView);

                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

                promptsView.findViewById(R.id.LinerZvon_item).setBackground(alertbackground);

                final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                final TextView textView = promptsView.findViewById(R.id.textView2);
                final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                final EditText Kab = promptsView.findViewById(R.id.numKab);
                final String[] help,helpop,helpyrok;

                help = textTime.split("-");
                zvonokone.setText(help[0].substring(0,5));
                zvonoktwo.setText(help[1].substring(1));
                helpop = textBottom.split(",");
                Yrok.setText(helpop[0]);
                helpyrok = helpop[1].split("№");
                Kab.setText(helpyrok[1]);
                final Spinner spinner = promptsView.findViewById(R.id.spinner);
                List<String> choose = new ArrayList<>();
                if(helpyrok[0].equals(" " + context.getString(R.string.classroomSchool) + " " )) {
                    textView.setText(context.getString(R.string.editLesson));
                    choose.add(context.getString(R.string.classroomSchool));
                    choose.add(context.getString(R.string.classroomUniversity));
                }else{
                    textView.setText(context.getString(R.string.editCouple));
                    choose.add(context.getString(R.string.classroomUniversity));
                    choose.add(context.getString(R.string.classroomSchool));
                }

                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context,choose,true);
                spinner.setAdapter(spinnerAdapter);
                spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
                newzvonok.setCancelable(true);
                final AlertDialog alertDialog = newzvonok.create();

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.hide();
                    }
                });

                TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String ZvonOne = zvonokone.getText().toString();
                        String ZvonTwo = zvonoktwo.getText().toString();
                        String NameYrok = Yrok.getText().toString();
                        String NumKab = Kab.getText().toString();
                        if (ZvonOne.length() == 5 && ZvonTwo.length() == 5 && NameYrok.length() > 0 && NumKab.length() > 0){
                            int TimeStartHour = 666;
                            int TimeStartMin = 666;
                            int TimeEndHour = 666;
                            int TimeEndMin = 666;
                            if (checkString(ZvonOne.substring(0,2)))
                                TimeStartHour = Integer.parseInt(ZvonOne.substring(0,2));
                            if(checkString(ZvonOne.substring(3)))
                                TimeStartMin = Integer.parseInt(ZvonOne.substring(3));
                            if(checkString(ZvonTwo.substring(0,2)))
                                TimeEndHour = Integer.parseInt(ZvonTwo.substring(0,2));
                            if(checkString(ZvonTwo.substring(3)))
                                TimeEndMin = Integer.parseInt(ZvonTwo.substring(3));


                            if(TimeStartHour < 25 && TimeStartMin < 60 && ZvonOne.charAt(2) == ':' && TimeEndHour < 25 && TimeEndMin < 60 && ZvonTwo.charAt(2) == ':') {
                                if ((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) {
                                    StringBuffer stringBuffer = new StringBuffer();
                                    try {
                                        boolean Zapic = true;

                                        StringBuffer stringBuffered = new StringBuffer();

                                        try {
                                            FileInputStream read = context.openFileInput(url);
                                            InputStreamReader reader = new InputStreamReader(read);
                                            BufferedReader bufferedReader = new BufferedReader(reader);
                                            String temp_read;
                                            String[] helpip ;
                                            String delimeter = "=";
                                            while ((temp_read = bufferedReader.readLine()) != null) {

                                                helpip = temp_read.split(delimeter);


                                                if  (!helpip[0].equals(help[0].substring(0,5) + " - " + help[1].substring(1)))
                                                    stringBuffered.append(temp_read).append("~");
                                            }

                                            bufferedReader.close();
                                            reader.close();
                                            read.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if(!stringBuffered.toString().equals("")) {
                                            String[] mas = stringBuffered.toString().split("~");
                                            for (int i = 0; i < mas.length; i++) {
                                                String[] helping;
                                                helping = mas[i].split("=");
                                                if ((Integer.parseInt(helping[0].substring(0, 2)) == TimeStartHour && Integer.parseInt(helping[0].substring(3, 5)) == TimeStartMin) || (Integer.parseInt(helping[0].substring(8, 10)) == TimeEndHour && Integer.parseInt(helping[0].substring(11)) == TimeEndMin)) {
                                                    throw new Povtor("Syko blyat", 1);
                                                }
                                                if (Integer.parseInt(helping[0].substring(0, 2)) > TimeStartHour && Zapic) {
                                                    stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab).append(("\n")).append(mas[i]).append(("\n"));
                                                    Zapic = false;

                                                } else
                                                    stringBuffer.append(mas[i]).append(("\n"));
                                            }

                                        }
                                        if (Zapic)
                                            stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab);

                                        try {
                                            FileOutputStream write =  context.openFileOutput(url, context.MODE_PRIVATE);
                                            String temp_write = stringBuffer.toString();

                                            write.write(temp_write.getBytes());
                                            write.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        product.get(position).changeText(ZvonOne + " - " + ZvonTwo, NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab);
                                        adapter.notifyDataSetChanged();

                                    } catch (Povtor povtor) {
                                        Toast.makeText(context,context.getString(R.string.timeSpan),Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                    Toast.makeText(context, context.getString(R.string.timeSpanStartEnd), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(
                                        context, context.getString(R.string.FieldsNot), Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                        else {
                            Toast.makeText(
                                    context, context.getString(R.string.wrongFormat), Toast.LENGTH_SHORT
                            ).show();
                        }

                        alertDialog.hide();
                    }
                });

                //VISUAL
                TextView Title = promptsView.findViewById(R.id.textView2);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                Title = promptsView.findViewById(R.id.text_time_start);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                Title = promptsView.findViewById(R.id.text_time_end);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                Title = promptsView.findViewById(R.id.text_mesto);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                Title = promptsView.findViewById(R.id.text_num_kab);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                Title = promptsView.findViewById(R.id.text_pred_name);
                Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                zvonokone.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                zvonoktwo.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                Yrok.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                Kab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                MainActivity.setCursorPointerColor(zvonokone,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorPointerColor(zvonoktwo,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorPointerColor(Yrok,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorPointerColor(Kab,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(zvonokone,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(zvonoktwo,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(Yrok,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(Kab,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                zvonokone.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                zvonoktwo.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                Yrok.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                Kab.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                alertDialog.show();
            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add))));
        Drawable drawableFAB = context.getDrawable(R.drawable.ic_add_24px);
        drawableFAB.setColorFilter(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)), PorterDuff.Mode.SRC_ATOP);
        floatingActionButton.setImageDrawable(drawableFAB);
        floatingActionButton.setOnClickListener(new View.OnClickListener()  {
                                      @Override
                                      public void onClick(final View view) {

                                          final LayoutInflater li = LayoutInflater.from(context);
                                          View promptsView = li.inflate(R.layout.prompt , null);
                                          final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                                          newzvonok.setView(promptsView);
                                          GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                                          alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                                          if(settings.getBoolean("BorderAlertSettings",false))
                                              alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

                                          promptsView.findViewById(R.id.LinerZvon_item).setBackground(alertbackground);

                                          final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                                          final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                                          final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                                          final EditText Kab = promptsView.findViewById(R.id.numKab);
                                          final Spinner spinner = promptsView.findViewById(R.id.spinner);

                                          List<String> choose = new ArrayList<>();
                                          choose.add(context.getString(R.string.classroomSchool));
                                          choose.add(context.getString(R.string.classroomUniversity));
                                          SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context,choose,true);
                                          spinner.setAdapter(spinnerAdapter);
                                          spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
                                          newzvonok.setCancelable(true);
                                          final AlertDialog alertDialog = newzvonok.create();

                                          TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                                          ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                                          ButtonCancel.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  alertDialog.hide();
                                              }
                                          });

                                          TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                                          ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                                          ButtonSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String ZvonOne = zvonokone.getText().toString();
                                                String ZvonTwo = zvonoktwo.getText().toString();
                                                String  NameYrok = Yrok.getText().toString();
                                                String NumKab = Kab.getText().toString();

                                                if ((ZvonOne.length() == 5 || ZvonOne.equals("")) && (ZvonTwo.length() == 5 || ZvonTwo.equals(""))){
                                                    if(ZvonOne.equals("")) ZvonOne = "08:00";
                                                    if(ZvonTwo.equals("")) ZvonTwo = "08:40";
                                                    if(NameYrok.equals("")) NameYrok = context.getString(R.string.lessonExample);
                                                    if(NumKab.equals("")) NumKab = "5";

                                                    int TimeStartHour = 666;
                                                    int TimeStartMin = 666;
                                                    int TimeEndHour = 666;
                                                    int TimeEndMin = 666;

                                                    if (checkString(ZvonOne.substring(0,2))) TimeStartHour = Integer.parseInt(ZvonOne.substring(0,2));
                                                    if(checkString(ZvonOne.substring(3))) TimeStartMin = Integer.parseInt(ZvonOne.substring(3));
                                                    if(checkString(ZvonTwo.substring(0,2))) TimeEndHour = Integer.parseInt(ZvonTwo.substring(0,2));
                                                    if(checkString(ZvonTwo.substring(3))) TimeEndMin = Integer.parseInt(ZvonTwo.substring(3));


                                                    if(TimeStartHour < 25 && TimeStartMin < 60 && ZvonOne.charAt(2) == ':' && TimeEndHour < 25 && TimeEndMin < 60 && ZvonTwo.charAt(2) == ':') {
                                                        if ((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) {

                                                            StringBuffer stringBuffer = new StringBuffer();

                                                            try {
                                                                boolean Zapic = true;
                                                                int NumString = 0;

                                                                int positionTek = 0;
                                                                String urlTek = "Monday.txt";
                                                                switch (settings.getString("Day","Monday.txt")){
                                                                    case "Tuesday.txt":
                                                                        positionTek = 1;
                                                                        urlTek = "Tuesday.txt";
                                                                        break;

                                                                    case "Wednesday.txt":
                                                                        positionTek = 2;
                                                                        urlTek = "Wednesday.txt";
                                                                        break;

                                                                    case "Thursday.txt":
                                                                        positionTek = 3;
                                                                        urlTek = "Thursday.txt";
                                                                        break;

                                                                    case "Friday.txt":
                                                                        positionTek = 4;
                                                                        urlTek = "Friday.txt";
                                                                        break;

                                                                    case "Saturday.txt":
                                                                        if(settings.getBoolean("SaturdaySettings",true)) {
                                                                            positionTek = 5;
                                                                            urlTek = "Saturday.txt";
                                                                        }
                                                                        break;
                                                                }

                                                                try {
                                                                    FileInputStream read =  context.openFileInput(urlTek);
                                                                    InputStreamReader reader = new InputStreamReader(read);
                                                                    BufferedReader bufferedReader = new BufferedReader(reader);

                                                                    String temp_read;
                                                                    String[] help;
                                                                    String delimeter = "=";
                                                                    int i = 0;
                                                                    while ((temp_read = bufferedReader.readLine()) != null) {

                                                                        help = temp_read.split(delimeter);
                                                                        if((Integer.parseInt(help[0].substring(0,2)) == TimeStartHour && Integer.parseInt(help[0].substring(3,5)) == TimeStartMin) || (Integer.parseInt(help[0].substring(8,10)) == TimeEndHour && Integer.parseInt(help[0].substring(11)) == TimeEndMin)) {
                                                                            throw new Povtor("KRIA", 1);
                                                                        }
                                                                        if(Integer.parseInt(help[0].substring(0,2)) > TimeStartHour   && Zapic) {
                                                                            stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab).append(("\n")).append(temp_read).append(("\n"));
                                                                            Zapic = false;
                                                                            NumString = i;
                                                                        } else {
                                                                            stringBuffer.append(temp_read).append(("\n"));
                                                                            i = i + 1;
                                                                        }
                                                                    }
                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                if (Zapic) {
                                                                    stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab);
                                                                    NumString = constrFragmentViewPagerArrayList.get(positionTek).getArray().size();
                                                                }
                                                                try {
                                                                    FileOutputStream write =  context.openFileOutput(urlTek, context.MODE_PRIVATE);
                                                                    String temp_write = stringBuffer.toString();

                                                                    write.write(temp_write.getBytes());
                                                                    write.close();
                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                if(constrFragmentViewPagerArrayList.get(positionTek).getArray().size() == 0)
                                                                    TextViewInisible(constrFragmentViewPagerArrayList.get(positionTek).getView());
                                                                constrFragmentViewPagerArrayList.get(positionTek).getArray().add(NumString,new ConstrRecyclerView(ZvonOne + " - " + ZvonTwo, NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab));
                                                                if(settings.getBoolean("AnimationSettings",true))
                                                                    constrFragmentViewPagerArrayList.get(positionTek).getRecyclerAdapter().notifyItemInserted(NumString);
                                                                else
                                                                    constrFragmentViewPagerArrayList.get(positionTek).getRecyclerAdapter().notifyDataSetChanged();

                                                                alertDialog.hide();

                                                            } catch (Povtor povtor) {
                                                                Toast.makeText(context,context.getString(R.string.timeSpan),Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                        else
                                                            Toast.makeText(context, context.getString(R.string.timeSpanStartEnd), Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(
                                                                context, context.getString(R.string.FieldsNot), Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(
                                                            context, context.getString(R.string.wrongFormat), Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                        });

                                          TextView Title = promptsView.findViewById(R.id.textView2);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          Title = promptsView.findViewById(R.id.text_time_start);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          Title = promptsView.findViewById(R.id.text_time_end);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          Title = promptsView.findViewById(R.id.text_mesto);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          Title = promptsView.findViewById(R.id.text_num_kab);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          Title = promptsView.findViewById(R.id.text_pred_name);
                                          Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                                          zvonokone.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                                          zvonoktwo.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                                          Yrok.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                                          Kab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                                          MainActivity.setCursorPointerColor(zvonokone,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorPointerColor(zvonoktwo,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorPointerColor(Yrok,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorPointerColor(Kab,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorColor(zvonokone,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorColor(zvonoktwo,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorColor(Yrok,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          MainActivity.setCursorColor(Kab,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                                          zvonokone.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                                          zvonoktwo.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                                          Yrok.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                                          Kab.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                                          alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                          alertDialog.show();

                                     }
                                  }
        );

        container.addView(view,0);
        return view;
    }

    public void TextViewVisible(View view) {
        TextView textView = view.findViewById(R.id.nullZvon);
        textView.setVisibility(View.VISIBLE);
        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
    }

    public void TextViewInisible(View view) {
        TextView textView = view.findViewById(R.id.nullZvon);
        textView.setVisibility(View.INVISIBLE);
    }

    public boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}