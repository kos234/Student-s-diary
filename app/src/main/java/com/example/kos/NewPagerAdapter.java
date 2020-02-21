package com.example.kos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apmem.tools.layouts.FlowLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

class NewPagerAdapter extends PagerAdapter {
    private final Context context;
    private final String[] name;
    private final SharedPreferences settings;
    private final SharedPreferences Current_Theme;
    private final List<ConstrFragmentViewPager> constrFragmentViewPagerArrayList;
    private final FloatingActionButton floatingActionButton;

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
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
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
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
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
                    public void onClick(View viewClick) {
                        StringBuilder stringBuffer = new StringBuilder();
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

                        } catch (IOException e) {

                        }



                        try {
                            FileOutputStream write = context.openFileOutput(url, MODE_PRIVATE);

                            write.write(stringBuffer.toString().getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {

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

                Objects.requireNonNull(Deleted.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Deleted.show();

            }
        });

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                final String textTime = product.get(position).getTextName();
                String textBottom = product.get(position).getTextBottom();

                final LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt , null);
                final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                newzvonok.setView(promptsView);

                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

                promptsView.findViewById(R.id.LinerZvon_item).setBackground(alertbackground);

                final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                final TextView textView = promptsView.findViewById(R.id.textView2);
                final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                final EditText Kab = promptsView.findViewById(R.id.numKab);
                final String[] help,helpop,helpyrok, timeOneAM, timeTwoAM;

                help = textTime.split(" - ");
                timeOneAM = help[0].split(" ");
                timeTwoAM = help[1].split(" ");

                zvonokone.setText(timeOneAM[0]);
                zvonoktwo.setText(timeTwoAM[0]);

                final boolean is12Hour = timeOneAM.length == 2 && timeTwoAM.length == 2;

                final Spinner spinnerAmPmOne = new Spinner(context), spinnerAmPmTwo = new Spinner(context);
                if(is12Hour){
                    FlowLayout flowLayoutFieldOne = promptsView.findViewById(R.id.field_times_add_one);
                    FlowLayout flowLayoutFieldTwo = promptsView.findViewById(R.id.field_times_add_two);

                    FlowLayout.LayoutParams layoutParamsSpinnerOne = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsSpinnerOne.setMargins(5,0,0,0);
                    FlowLayout.LayoutParams layoutParamsSpinnerTwo = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsSpinnerTwo.setMargins(5,0,0,0);

                    List<String> AmPmListOne = new ArrayList<>();
                    switch (timeOneAM[1]){
                        case "AM":
                            AmPmListOne.add("AM");
                            AmPmListOne.add("PM");
                            break;

                        case "PM":
                            AmPmListOne.add("PM");
                            AmPmListOne.add("AM");
                            break;
                    }

                    List<String> AmPmListTwo = new ArrayList<>();
                    switch (timeTwoAM[1]){
                        case "AM":
                            AmPmListTwo.add("AM");
                            AmPmListTwo.add("PM");
                            break;

                        case "PM":
                            AmPmListTwo.add("PM");
                            AmPmListTwo.add("AM");
                            break;
                    }

                    SpinnerAdapter spinnerAdapterAmPmOne = new SpinnerAdapter(context,AmPmListOne,true);
                    SpinnerAdapter spinnerAdapterAmPmTwo = new SpinnerAdapter(context,AmPmListTwo,true);
                    spinnerAmPmOne.setAdapter(spinnerAdapterAmPmOne);
                    spinnerAmPmTwo.setAdapter(spinnerAdapterAmPmTwo);
                    spinnerAmPmOne.setLayoutParams(layoutParamsSpinnerOne);
                    spinnerAmPmTwo.setLayoutParams(layoutParamsSpinnerTwo);
                    spinnerAmPmOne.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
                    spinnerAmPmTwo.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);

                    flowLayoutFieldOne.addView(spinnerAmPmOne);
                    flowLayoutFieldTwo.addView(spinnerAmPmTwo);

                }

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


                        if(((TimeStartHour < 25 && TimeEndHour < 25 && !is12Hour)) || (TimeStartHour < 13 && TimeEndHour < 13 && is12Hour) && TimeStartMin < 60 && ZvonOne.charAt(2) == ':' && TimeEndMin < 60 && ZvonTwo.charAt(2) == ':') {
                            if (((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) && !is12Hour || is12Hour &&  !(spinnerAmPmOne.getSelectedItem().equals("PM") && spinnerAmPmTwo.getSelectedItem().equals("AM")) && ((TimeStartHour < TimeEndHour || (spinnerAmPmOne.getSelectedItem().equals("AM") && spinnerAmPmTwo.getSelectedItem().equals("PM"))) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin))) {
                                    StringBuilder stringBuffer = new StringBuilder();
                                    try {
                                        boolean Zapic = true;
                                        int NumString = 0;
                                        String writeTimes;
                                        String[] viewTimes = new String[2];
                                        if(is12Hour) {
                                            writeTimes = ZvonOne + ":" + spinnerAmPmOne.getSelectedItem() + "-" + ZvonTwo + ":" + spinnerAmPmTwo.getSelectedItem() + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                            viewTimes[0] = ZvonOne + " " + spinnerAmPmOne.getSelectedItem() + " - " + ZvonTwo + " " + spinnerAmPmTwo.getSelectedItem();
                                            viewTimes[1] = NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                        }else {
                                            writeTimes = ZvonOne + "-" + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                            viewTimes[0] = ZvonOne + " - " + ZvonTwo;
                                            viewTimes[1] = NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                        }

                                        try {
                                            FileInputStream read = context.openFileInput(url);
                                            InputStreamReader reader = new InputStreamReader(read);
                                            BufferedReader bufferedReader = new BufferedReader(reader);
                                            String temp_read;
                                            String[] help,helpTimes, helpAmPMOne, helpAmPMTwo;
                                            String delimeter = "=";

                                            int i = 0;
                                            while ((temp_read = bufferedReader.readLine()) != null) {
                                                if(position != i){
                                                help = temp_read.split(delimeter);
                                                helpTimes = help[0].split("-");
                                                helpAmPMOne = helpTimes[0].split(":");
                                                helpAmPMTwo = helpTimes[1].split(":");


                                                if((Integer.parseInt(helpAmPMOne[0]) == TimeStartHour && Integer.parseInt(helpAmPMOne[1]) == TimeStartMin) && (Integer.parseInt(helpAmPMTwo[0]) == TimeEndHour && Integer.parseInt(helpAmPMTwo[1]) == TimeEndMin))
                                                    if(is12Hour) {
                                                        if (helpAmPMOne[2].equals(spinnerAmPmOne.getSelectedItem()) && helpAmPMTwo[2].equals(spinnerAmPmTwo.getSelectedItem()))
                                                            throw new Povtor("KRIA");
                                                    }else throw new Povtor("KRIA");

                                                if((Integer.parseInt(helpAmPMOne[0]) > TimeStartHour || Integer.parseInt(helpAmPMOne[1]) > TimeStartMin) && !is12Hour && Zapic) {
                                                    stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                                    Zapic = false;
                                                    NumString = i;
                                                } else if(is12Hour && Zapic)
                                                    if((helpAmPMOne[2].equals("AM") && spinnerAmPmOne.getSelectedItem().equals("AM") || helpAmPMOne[2].equals("PM") && spinnerAmPmOne.getSelectedItem().equals("PM")) && (Integer.parseInt(helpAmPMOne[0]) > TimeStartHour || (Integer.parseInt(helpAmPMOne[1]) > TimeStartMin && Integer.parseInt(helpAmPMOne[0]) == TimeStartHour))){
                                                    stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                                    Zapic = false;
                                                    NumString = i;
                                                }else {
                                                    stringBuffer.append(temp_read).append(("\n"));
                                                    i = i + 1;
                                                }
//                                                if((Integer.parseInt(helpAmPMOne[0]) == Integer.parseInt(timeOneAM[0].substring(0,2)) && Integer.parseInt(helpAmPMOne[1]) == Integer.parseInt(timeOneAM[0].substring(3))) || (Integer.parseInt(helpAmPMTwo[0]) == Integer.parseInt(timeTwoAM[0].substring(0,2)) && Integer.parseInt(helpAmPMTwo[1]) == Integer.parseInt(timeTwoAM[0].substring(3)))){
//                                                    if(is12Hour) {
//                                                        if (helpAmPMOne[2].equals(timeOneAM[1]) && helpAmPMTwo[2].equals(timeTwoAM[1]))
//                                                            stringBuffer.append(writeTimes).append(("\n"));
//                                                    }else stringBuffer.append(writeTimes).append(("\n"));
//
//                                                }else
//                                                    stringBuffer.append(temp_read).append(("\n"));

                                            }else i = i + 1;
                                            }

                                            bufferedReader.close();
                                            reader.close();
                                            read.close();
                                        } catch (FileNotFoundException e) {

                                        } catch (IOException e) {

                                        }

                                        if (Zapic) {
                                            stringBuffer.append(writeTimes);
                                            NumString = product.size();
                                        }

                                        try {
                                            FileOutputStream write =  context.openFileOutput(url, MODE_PRIVATE);
                                            String temp_write = stringBuffer.toString();

                                            write.write(temp_write.getBytes());
                                            write.close();
                                        } catch (FileNotFoundException e) {

                                        } catch (IOException e) {

                                        }

                                        product.get(position).changeText(viewTimes[0], viewTimes[1]);
                                        adapter.onMove(position, NumString);

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
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                alertDialog.show();
            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add))));
        Drawable drawableFAB = context.getDrawable(R.drawable.ic_add_24px);
        Objects.requireNonNull(drawableFAB).setColorFilter(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)), PorterDuff.Mode.SRC_ATOP);
        floatingActionButton.setImageDrawable(drawableFAB);
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                final LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.alert_delete_dnewnik, null);
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setView(promptsView);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (settings.getBoolean("BorderAlertSettings", false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings", 4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

                final AlertDialog Deleted = Delete.create();

                TextView textTitle = promptsView.findViewById(R.id.title_alert);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                textTitle.setText(context.getString(R.string.deleting));

                TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textBottomTitle.setText(context.getString(R.string.deleteAllLesson));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setText(context.getString(R.string.cancel));
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Deleted.hide();
                    }
                });

                TextView ButtonALl = promptsView.findViewById(R.id.button_three_alert);
                ButtonALl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewClick) {
                        String[] day = context.getResources().getStringArray(R.array.DayTxt);
                        for (String s : day) {

                            File outFile = new File(context.getFilesDir() + "/" + s);
                            if (outFile.exists()) {
                                outFile.delete();
                            }
                        }
                        ZnonkiFragment znonkiFragment = (ZnonkiFragment) ((MainActivity) context).getFragment.get(2);
                        znonkiFragment.DeleteAll();

                        Deleted.hide();

                    }
                });
                ButtonALl.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonALl.setText(context.getString(R.string.deleteAllDayLesson));

                TextView ButtonOne = promptsView.findViewById(R.id.button_two_alert);
                ButtonOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewClick) {
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

                        File outFile = new File(context.getFilesDir() + "/" + urlTek);
                        if (outFile.exists()) {
                            outFile.delete();
                        }
                        if(settings.getBoolean("AnimationSettings",true))
                            new AnimationDel().execute(constrFragmentViewPagerArrayList.get(positionTek));
                        else {
                            constrFragmentViewPagerArrayList.get(positionTek).getArray().clear();
                            constrFragmentViewPagerArrayList.get(positionTek).getRecyclerAdapter().notifyDataSetChanged();
                        }

                        Deleted.hide();

                    }
                });
                ButtonOne.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonOne.setText(context.getString(R.string.deleteOneDayLesson));

                Objects.requireNonNull(Deleted.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Deleted.show();
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener()  {
                                      @Override
                                      public void onClick(final View view) {

                                          final LayoutInflater li = LayoutInflater.from(context);
                                          View promptsView = li.inflate(R.layout.prompt , null);
                                          final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                                          newzvonok.setView(promptsView);
                                          GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                                          Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
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

                                          final Spinner spinnerAmPmOne = new Spinner(context), spinnerAmPmTwo = new Spinner(context);
                                          if(!DateFormat.is24HourFormat(context)){
                                              FlowLayout flowLayoutFieldOne = promptsView.findViewById(R.id.field_times_add_one);
                                              FlowLayout flowLayoutFieldTwo = promptsView.findViewById(R.id.field_times_add_two);

                                              FlowLayout.LayoutParams layoutParamsSpinnerOne = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                              layoutParamsSpinnerOne.setMargins(5,0,0,0);
                                              FlowLayout.LayoutParams layoutParamsSpinnerTwo = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                              layoutParamsSpinnerTwo.setMargins(5,0,0,0);

                                              List<String> AmPmList = new ArrayList<>();
                                              AmPmList.add("AM");
                                              AmPmList.add("PM");

                                              SpinnerAdapter spinnerAdapterAmPm = new SpinnerAdapter(context,AmPmList,true);
                                              spinnerAmPmOne.setAdapter(spinnerAdapterAmPm);
                                              spinnerAmPmTwo.setAdapter(spinnerAdapterAmPm);
                                              spinnerAmPmOne.setLayoutParams(layoutParamsSpinnerOne);
                                              spinnerAmPmTwo.setLayoutParams(layoutParamsSpinnerTwo);
                                              spinnerAmPmOne.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
                                              spinnerAmPmTwo.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);

                                              flowLayoutFieldOne.addView(spinnerAmPmOne);
                                              flowLayoutFieldTwo.addView(spinnerAmPmTwo);

                                          }

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


                                                    if(((TimeStartHour < 25 && TimeEndHour < 25 && DateFormat.is24HourFormat(context))) || (TimeStartHour < 13 && TimeEndHour < 13 && !DateFormat.is24HourFormat(context)) && TimeStartMin < 60 && ZvonOne.charAt(2) == ':' && TimeEndMin < 60 && ZvonTwo.charAt(2) == ':') {
                                                        if (((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) && DateFormat.is24HourFormat(context) || !DateFormat.is24HourFormat(context) && !(spinnerAmPmOne.getSelectedItem().equals("PM") && spinnerAmPmTwo.getSelectedItem().equals("AM")) &&  ((TimeStartHour < TimeEndHour || (spinnerAmPmOne.getSelectedItem().equals("AM") && spinnerAmPmTwo.getSelectedItem().equals("PM"))) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin))) {

                                                            StringBuilder stringBuffer = new StringBuilder();

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

                                                                String writeTimes;
                                                                String[] viewTimes = new String[2];
                                                                if(!DateFormat.is24HourFormat(context)) {
                                                                    writeTimes = ZvonOne + ":" + spinnerAmPmOne.getSelectedItem() + "-" + ZvonTwo + ":" + spinnerAmPmTwo.getSelectedItem() + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                                                    viewTimes[0] = ZvonOne + " " + spinnerAmPmOne.getSelectedItem() + " - " + ZvonTwo + " " + spinnerAmPmTwo.getSelectedItem();
                                                                    viewTimes[1] = NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                                                }else {
                                                                    writeTimes = ZvonOne + "-" + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                                                    viewTimes[0] = ZvonOne + " - " + ZvonTwo;
                                                                    viewTimes[1] = NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab;
                                                                }
                                                                try {
                                                                    FileInputStream read =  context.openFileInput(urlTek);
                                                                    InputStreamReader reader = new InputStreamReader(read);
                                                                    BufferedReader bufferedReader = new BufferedReader(reader);

                                                                    String temp_read;
                                                                    String[] help,helpTimes, helpAmPMOne, helpAmPMTwo;
                                                                    String delimeter = "=";
                                                                    int i = 0;

                                                                    while ((temp_read = bufferedReader.readLine()) != null) {

                                                                        help = temp_read.split(delimeter);
                                                                        helpTimes = help[0].split("-");
                                                                        helpAmPMOne = helpTimes[0].split(":");
                                                                        helpAmPMTwo = helpTimes[1].split(":");
                                                                        if((Integer.parseInt(helpAmPMOne[0]) == TimeStartHour && Integer.parseInt(helpAmPMOne[1]) == TimeStartMin) || (Integer.parseInt(helpAmPMTwo[0]) == TimeEndHour && Integer.parseInt(helpAmPMTwo[1]) == TimeEndMin))
                                                                            if(!DateFormat.is24HourFormat(context)) {
                                                                                if (helpAmPMOne[2].equals(spinnerAmPmOne.getSelectedItem()) && helpAmPMTwo[2].equals(spinnerAmPmTwo.getSelectedItem()))
                                                                                    throw new Povtor("KRIA");
                                                                            }else throw new Povtor("KRIA");

                                                                        if((Integer.parseInt(helpAmPMOne[0]) > TimeStartHour || Integer.parseInt(helpAmPMOne[1]) > TimeStartMin) && DateFormat.is24HourFormat(context) && Zapic) {
                                                                            stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                                                            Zapic = false;
                                                                            NumString = i;
                                                                        } else if(!DateFormat.is24HourFormat(context) && Zapic)
                                                                            if((helpAmPMOne[2].equals("AM") && spinnerAmPmOne.getSelectedItem().equals("AM") || helpAmPMOne[2].equals("PM") && spinnerAmPmOne.getSelectedItem().equals("PM")) && (Integer.parseInt(helpAmPMOne[0]) > TimeStartHour || (Integer.parseInt(helpAmPMOne[1]) > TimeStartMin && Integer.parseInt(helpAmPMOne[0]) == TimeStartHour))){
                                                                                stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                                                            Zapic = false;
                                                                            NumString = i;
                                                                        }else {
                                                                            stringBuffer.append(temp_read).append(("\n"));
                                                                            i = i + 1;
                                                                        }
                                                                    }
                                                                } catch (FileNotFoundException e) {

                                                                } catch (IOException e) {

                                                                }
                                                                if (Zapic) {
                                                                    stringBuffer.append(writeTimes);
                                                                    NumString = constrFragmentViewPagerArrayList.get(positionTek).getArray().size();
                                                                }
                                                                try {
                                                                    FileOutputStream write =  context.openFileOutput(urlTek, MODE_PRIVATE);
                                                                    String temp_write = stringBuffer.toString();

                                                                    write.write(temp_write.getBytes());
                                                                    write.close();
                                                                } catch (FileNotFoundException e) {

                                                                } catch (IOException e) {

                                                                }
                                                                if(constrFragmentViewPagerArrayList.get(positionTek).getArray().size() == 0)
                                                                    TextViewInisible(constrFragmentViewPagerArrayList.get(positionTek).getView());
                                                                constrFragmentViewPagerArrayList.get(positionTek).getArray().add(NumString,new ConstrRecyclerView(viewTimes[0] , viewTimes[1]));
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
                                          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                          alertDialog.show();

                                     }
                                  }
        );

        container.addView(view,0);
        return view;
    }

    private void TextViewVisible(View view) {
        TextView textView = view.findViewById(R.id.nullZvon);
        textView.setVisibility(View.VISIBLE);
        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
    }

    private void TextViewInisible(View view) {
        TextView textView = view.findViewById(R.id.nullZvon);
        textView.setVisibility(View.INVISIBLE);
    }

    class AnimationDel extends AsyncTask<ConstrFragmentViewPager,Integer,Void>{
        ConstrFragmentViewPager constrFragmentViewPager;
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            constrFragmentViewPager.getRecyclerAdapter().notifyItemRemoved(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextViewVisible(constrFragmentViewPager.getView());
        }

        @Override
        protected Void doInBackground(ConstrFragmentViewPager... voids) {
            constrFragmentViewPager = voids[0];
            for (int l = constrFragmentViewPager.getArray().size() - 1; l >= 0; l--){
                constrFragmentViewPager.getArray().remove(l);
                publishProgress(l);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            return null;
        }
    }

    private boolean checkString(String string) {
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