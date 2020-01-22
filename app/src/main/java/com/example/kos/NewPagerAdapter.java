package com.example.kos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
    List<ConstrFragmentViewPager> constrFragmentViewPagerArrayList;
    public FloatingActionButton floatingActionButton;

    public NewPagerAdapter(List<ConstrFragmentViewPager> constrFragmentViewPagerArrayList, Context context,FloatingActionButton floatingActionButton) {
        this.constrFragmentViewPagerArrayList = constrFragmentViewPagerArrayList;
        this.context = context;
        name = new String[] {
                context.getString(R.string.monday),
                context.getString(R.string.tuesday),
                context.getString(R.string.wednesday),
                context.getString(R.string.thursday),
                context.getString(R.string.friday),
                context.getString(R.string.saturday)
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

        if(product.size() != 0){
            TextView textView = view.findViewById(R.id.nullZvon);
            textView.setVisibility(View.INVISIBLE);
        }
        final SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        final String url = constrFragmentViewPagerArrayList.get(position).getUrl();

        adapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {

                final String textTime = product.get(position).getTextName();
                AlertDialog.Builder deleted = new AlertDialog.Builder(context);
                deleted.setCancelable(true).setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer stringBuffer = new StringBuffer();

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

                    }
                })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = deleted.create();
                alertDialog.setTitle(context.getString(R.string.deleteLesson));
                alertDialog.show();
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
                List<String> choose = new ArrayList<String>();
                if(helpyrok[0].equals(" " + context.getString(R.string.classroomSchool) + " " )) {
                    textView.setText(context.getString(R.string.editLesson));
                    choose.add(context.getString(R.string.classroomSchool));
                    choose.add(context.getString(R.string.classroomUniversity));
                }else{
                    textView.setText(context.getString(R.string.editCouple));
                    choose.add(context.getString(R.string.classroomUniversity));
                    choose.add(context.getString(R.string.classroomSchool));
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>  (context,R.layout.spinner_list, choose);
                spinner.setAdapter(dataAdapter);
                newzvonok
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.save),
                                new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    public void onClick(DialogInterface dialog, int id) {
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
                                    }
                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = newzvonok.create();

                //и отображаем его:
                // alertDialog.setTitle("Новый урок");
                alertDialog.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener()  {
                                      @Override
                                      public void onClick(final View view) {

                                          final LayoutInflater li = LayoutInflater.from(context);
                                          View promptsView = li.inflate(R.layout.prompt , null);
                                          final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                                          newzvonok.setView(promptsView);

                                          final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                                          final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                                          final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                                          final EditText Kab = promptsView.findViewById(R.id.numKab);
                                          final Spinner spinner = promptsView.findViewById(R.id.spinner);

                                          List<String> choose = new ArrayList<>();
                                          choose.add(context.getString(R.string.classroomSchool));
                                          choose.add(context.getString(R.string.classroomUniversity));
                                          ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,R.layout.spinner_list, choose);
                                          spinner.setAdapter(dataAdapter);

                                          newzvonok
                                                  .setCancelable(true)
                                                  .setPositiveButton(context.getString(R.string.save),
                                                          new DialogInterface.OnClickListener() {
                                                              @RequiresApi(api = Build.VERSION_CODES.N)
                                                              public void onClick(DialogInterface dialog, int id) {

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
                                                                                          positionTek = 5;
                                                                                          urlTek = "Saturday.txt";
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

                                          //Создаем AlertDialog:
                                          AlertDialog alertDialog = newzvonok.create();

                                          //и отображаем его:
                                          // alertDialog.setTitle("Новый урок");
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
