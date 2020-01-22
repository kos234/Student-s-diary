package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ItemFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ConstrRecyclerView> products = new ArrayList<>();
    private String ZvonOne, ZvonTwo, NameYrok, NumKab;
    private String url;
    private SharedPreferences settings;
    private Context context;
    ViewGroup viewGroup;

    public ItemFragment(String url) {
        this.url = url;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_item_pager, container,false);
        Start(context);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        recyclerView = viewGroup.findViewById(R.id.Zvonki_Recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        Buttons();

        return viewGroup;
    }

    public void Buttons (){
        url = settings.getString("Day","Monday.txt");
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.floatingActionButton);
//        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(final View view) {
//                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
//                Delete.setMessage(context.getString(R.string.deleteAllLesson))
//                        .setCancelable(true)
//                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                String[] day = getResources().getStringArray(R.array.DayTxt);
//                                for (int k = 0; k < day.length; k++) {
//
//                                   /* File outFile = new File(day[k]);
//                                    if (outFile.exists()) {
//                                        outFile.delete();
//                                    } */
//                                }
//                                if(settings.getBoolean("AnimationSettings",true))
//                                    new AnimationDel().execute();
//                                else {
//                                    products.clear();
//                                    adapter.notifyDataSetChanged();
//                                }
//
//                            }
//                        })
//                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.cancel();
//
//                            }
//                        });
//
//                AlertDialog Deleted = Delete.create();
//                Deleted.setTitle(context.getString(R.string.deleting));
//                Deleted.show();
//                return false;
//            }
//        });
//        floatingActionButton.setOnClickListener(new View.OnClickListener()  {
//                                      @Override
//                                      public void onClick(final View view) {
//
//                                          Toast.makeText(context,url,Toast.LENGTH_LONG).show();
//
//                                          final LayoutInflater li = LayoutInflater.from(context);
//                                          View promptsView = li.inflate(R.layout.prompt , null);
//                                          final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
//                                          newzvonok.setView(promptsView);
//
//                                          final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
//                                          final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
//                                          final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
//                                          final EditText Kab = promptsView.findViewById(R.id.numKab);
//                                          final Spinner spinner = promptsView.findViewById(R.id.spinner);
//
//                                          List<String> choose = new ArrayList<>();
//                                          choose.add(context.getString(R.string.classroomSchool));
//                                          choose.add(context.getString(R.string.classroomUniversity));
//                                          ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_list, choose);
//                                          spinner.setAdapter(dataAdapter);
//
//                                          newzvonok
//                                                  .setCancelable(true)
//                                                  .setPositiveButton(context.getString(R.string.save),
//                                                          new DialogInterface.OnClickListener() {
//                                                              @RequiresApi(api = Build.VERSION_CODES.N)
//                                                              public void onClick(DialogInterface dialog, int id) {
//
//                                                                  ZvonOne = zvonokone.getText().toString();
//                                                                  ZvonTwo = zvonoktwo.getText().toString();
//                                                                  NameYrok = Yrok.getText().toString();
//                                                                  NumKab = Kab.getText().toString();
//
//                                                                  if ((ZvonOne.length() == 5 || ZvonOne.equals("")) && (ZvonTwo.length() == 5 || ZvonTwo.equals(""))){
//                                                                      if(ZvonOne.equals("")) ZvonOne = "08:00";
//                                                                      if(ZvonTwo.equals("")) ZvonTwo = "08:40";
//                                                                      if(NameYrok.equals("")) NameYrok = context.getString(R.string.lessonExample);
//                                                                      if(NumKab.equals("")) NumKab = "5";
//
//                                                                      int TimeStartHour = 666;
//                                                                      int TimeStartMin = 666;
//                                                                      int TimeEndHour = 666;
//                                                                      int TimeEndMin = 666;
//
//                                                                      if (checkString(ZvonOne.substring(0,2))) TimeStartHour = Integer.parseInt(ZvonOne.substring(0,2));
//                                                                      if(checkString(ZvonOne.substring(3))) TimeStartMin = Integer.parseInt(ZvonOne.substring(3));
//                                                                      if(checkString(ZvonTwo.substring(0,2))) TimeEndHour = Integer.parseInt(ZvonTwo.substring(0,2));
//                                                                      if(checkString(ZvonTwo.substring(3))) TimeEndMin = Integer.parseInt(ZvonTwo.substring(3));
//
//
//                                                                      if(TimeStartHour < 25 && TimeStartMin < 60 && ZvonOne.charAt(2) == ':' && TimeEndHour < 25 && TimeEndMin < 60 && ZvonTwo.charAt(2) == ':') {
//                                                                          if ((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) {
//
//                                                                              StringBuffer stringBuffer = new StringBuffer();
//
//                                                                              try {
//                                                                                  boolean Zapic = true;
//
//                                                                                  try {
//                                                                                      FileInputStream read =  context.openFileInput(url);
//                                                                                      InputStreamReader reader = new InputStreamReader(read);
//                                                                                      BufferedReader bufferedReader = new BufferedReader(reader);
//
//                                                                                      String temp_read;
//                                                                                      String[] help;
//                                                                                      String delimeter = "=";
//                                                                                      while ((temp_read = bufferedReader.readLine()) != null) {
//
//                                                                                          help = temp_read.split(delimeter);
//                                                                                          if((Integer.parseInt(help[0].substring(0,2)) == TimeStartHour && Integer.parseInt(help[0].substring(3,5)) == TimeStartMin) || (Integer.parseInt(help[0].substring(8,10)) == TimeEndHour && Integer.parseInt(help[0].substring(11)) == TimeEndMin)) {
//                                                                                              throw new Povtor("KRIA", 1);
//                                                                                          }
//                                                                                          if(Integer.parseInt(help[0].substring(0,2)) > TimeStartHour   && Zapic) {
//                                                                                              stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab).append(("\n")).append(temp_read).append(("\n"));
//                                                                                          Zapic = false;
//
//                                                                                          } else
//                                                                                          stringBuffer.append(temp_read).append(("\n"));
//                                                                                      }
//                                                                                  } catch (FileNotFoundException e) {
//                                                                                      e.printStackTrace();
//                                                                                  } catch (IOException e) {
//                                                                                      e.printStackTrace();
//                                                                                  }
//                                                                                    if (Zapic)
//                                                                                        stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab);
//                                                                                  try {
//                                                                                      FileOutputStream write =  context.openFileOutput(url, getActivity().MODE_PRIVATE);
//                                                                                      String temp_write = stringBuffer.toString();
//
//                                                                                      write.write(temp_write.getBytes());
//                                                                                      write.close();
//                                                                                  } catch (FileNotFoundException e) {
//                                                                                      e.printStackTrace();
//                                                                                  } catch (IOException e) {
//                                                                                      e.printStackTrace();
//                                                                                  }
//
//                                                                                      products.add(products.size(),new ConstrRecyclerView(ZvonOne + " - " + ZvonTwo , NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab));
//                                                                                      if(settings.getBoolean("AnimationSettings",true))
//                                                                                          adapter.notifyItemInserted(products.size() - 1);
//                                                                                      else
//                                                                                          adapter.notifyDataSetChanged();
//
//                                                                          } catch (Povtor povtor) {
//                                                                                Toast.makeText(context,context.getString(R.string.timeSpan),Toast.LENGTH_LONG).show();
//                                                                              }
//                                                                          }
//                                                                          else
//                                                                              Toast.makeText(context, context.getString(R.string.timeSpanStartEnd), Toast.LENGTH_SHORT).show();
//                                                                      }else{
//                                                                          Toast.makeText(
//                                                                                  context, context.getString(R.string.FieldsNot), Toast.LENGTH_SHORT
//                                                                          ).show();
//                                                                      }
//                                                                  }
//                                                                  else {
//                                                                      Toast.makeText(
//                                                                              context, context.getString(R.string.wrongFormat), Toast.LENGTH_SHORT
//                                                                      ).show();
//                                                                  }
//                                                              }
//                                                          });
//
//                                          //Создаем AlertDialog:
//                                          AlertDialog alertDialog = newzvonok.create();
//
//                                          //и отображаем его:
//                                          // alertDialog.setTitle("Новый урок");
//                                          alertDialog.show();
//
//                                     }
//                                  }
//        );
    }

    class AnimationDel extends AsyncTask<Void,Integer,Void> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            adapter.notifyItemRemoved(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextViewVisible();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int l = products.size() - 1; l >= 0; l--){
                products.remove(l);
                publishProgress(l);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            return null;
        }
    }

    public boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public void TextViewVisible() {
        TextView textView = viewGroup.findViewById(R.id.nullZvon);
        textView.setVisibility(View.VISIBLE);
    }

    public void AddCard(String textTime, String textBottom, boolean AnimChecked, Context contextActiv){
        Start(contextActiv);
        products.add(products.size(),new ConstrRecyclerView(textTime, textBottom));
        if(AnimChecked)
            adapter.notifyItemInserted(products.size() - 1);
        else
            adapter.notifyDataSetChanged();
    }

    public void Start(Context contextActiv) {
        String[] help ;
        String delimeter = "=";
        products.clear();
        try {
            FileInputStream read = contextActiv.openFileInput(url);
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
                help = temp_read.split(delimeter);
               products.add(new ConstrRecyclerView(help[0], help[1]));
            }
            bufferedReader.close();
            reader.close();
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {
            ignore.printStackTrace();
        }

        adapter = new RecyclerAdapter(products);

        if(products.size() != 0){
            TextView textView = viewGroup.findViewById(R.id.nullZvon);
            textView.setVisibility(View.INVISIBLE);
        }

    }



}