package com.example.kos;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class classTuesday extends Fragment {
    private ListView lvMain;
    ArrayList<HashMap<String, String>> products = new ArrayList<>();
    HashMap<String,String> map;
    Integer num = 0;

    private SharedPreferences settings;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (getActivity()!=null)) {
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            if (settings.contains("Tuesday")) {

                ImageButton imageButton = getActivity().findViewById(R.id.onOff);
                if (settings.getBoolean("Tuesday", true))
                    imageButton.setImageResource(R.drawable.on);
                else
                    imageButton.setImageResource(R.drawable.off);

            }
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Day","Tuesday.txt" );
            editor.apply();


        }




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.tuesday, container,false);
        Start();
        lvMain = viewGroup.findViewById(R.id.lvTuesday);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                new String[]{"Times", "Kab"},
                new int[]{R.id.textView1, R.id.textView1_2});
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final TextView textView = view.findViewById(R.id.textView1);
                AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setMessage("Удалить урок?").setCancelable(true).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer stringBuffer = new StringBuffer();

                        try {
                            FileInputStream read = getActivity().openFileInput("Tuesday.txt");
                            InputStreamReader reader = new InputStreamReader(read);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            String temp_read;
                            String[] help ;
                            String delimeter = "=";
                            while ((temp_read = bufferedReader.readLine()) != null) {

                                help = temp_read.split(delimeter);


                                if  (!help[0].equals(textView.getText()))
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
                            FileOutputStream write = getActivity().openFileOutput("Tuesday.txt",getActivity().MODE_PRIVATE);

                            write.write(stringBuffer.toString().getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Start();
                        lvMain = viewGroup.findViewById(R.id.lvTuesday);
                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                                new String[]{"Times", "Kab"},
                                new int[]{R.id.textView1,R.id.textView1_2});
                        lvMain.setAdapter(adapter);


                    }
                })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = deleted.create();
                alertDialog.setTitle("Удаление урока");
                alertDialog.show();
            }
        });
        return viewGroup;
    }
    public void Start() {
        String[] help ;
        String delimeter = "=";
        products.clear();
        try {
            FileInputStream read = getActivity().openFileInput("Tuesday.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
                help = temp_read.split(delimeter);
                map = new HashMap<>();
                map.put("Times", help[0]);
                map.put("Kab", help[1]);
                products.add(map);

            }
            bufferedReader.close();
            reader.close();
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignore) {

        }

        num = 0;

    }



}