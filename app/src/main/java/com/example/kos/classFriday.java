package com.example.kos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.annotation.RequiresApi;
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


public class classFriday extends Fragment {
    private ListView lvMain;
    private ArrayList<HashMap<String, String>> products = new ArrayList<>();
    private String ZvonOne, ZvonTwo, NameYrok, NumKab;
    private  HashMap<String,String> map;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.friday, container,false);
        Start();
        lvMain = viewGroup.findViewById(R.id.lvFriday);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                new String[]{"Times", "Kab"},
                new int[]{R.id.textView1,R.id.textView1_2});
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
                            FileInputStream read = getActivity().openFileInput("Friday.txt");
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
                            FileOutputStream write = getActivity().openFileOutput("Friday.txt",getActivity().MODE_PRIVATE);

                            write.write(stringBuffer.toString().getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                                Start();
                                lvMain = viewGroup.findViewById(R.id.lvFriday);
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
        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewZvon = view.findViewById(R.id.textView1);
                final TextView textViewKab = view.findViewById(R.id.textView1_2);
                final LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.prompt , null);
                final AlertDialog.Builder newzvonok = new AlertDialog.Builder(getActivity());
                newzvonok.setView(promptsView);
                final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                final TextView textView = promptsView.findViewById(R.id.textView2);
                final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                final EditText Kab = promptsView.findViewById(R.id.numKab);
                final String[] help,helpop ;
                help = textViewZvon.getText().toString().split("-");
                zvonokone.setText(help[0].substring(0,5));
                zvonoktwo.setText(help[1].substring(1));
                helpop = textViewKab.getText().toString().split(",");
                Yrok.setText(helpop[0]);
                Kab.setText(helpop[1].substring(10));
                textView.setText("Изменение урока/пары");
                newzvonok
                        .setCancelable(true)
                        .setPositiveButton("Редактировать",
                                new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    public void onClick(DialogInterface dialog, int id) {
                                        ZvonOne = zvonokone.getText().toString();
                                        ZvonTwo = zvonoktwo.getText().toString();
                                        NameYrok = Yrok.getText().toString();
                                        NumKab = Kab.getText().toString();
                                        if (ZvonOne.length() == 5 && ZvonTwo.length() == 5 && NameYrok.length() > 0 && NumKab.length() > 0){
                                            int ZvonOneOne = 666;
                                            int ZvonOneTwo = 666;
                                            int ZvonTwoOne = 666;
                                            int ZvonTwoTwo = 666;
                                            if (checkString(ZvonOne.substring(0,2)))
                                                ZvonOneOne = Integer.parseInt(ZvonOne.substring(0,2));
                                            if(checkString(ZvonOne.substring(3)))
                                                ZvonOneTwo = Integer.parseInt(ZvonOne.substring(3));
                                            if(checkString(ZvonTwo.substring(0,2)))
                                                ZvonTwoOne = Integer.parseInt(ZvonTwo.substring(0,2));
                                            if(checkString(ZvonTwo.substring(3)))
                                                ZvonTwoTwo = Integer.parseInt(ZvonTwo.substring(3));


                                            if(ZvonOneOne < 25 && ZvonOneTwo < 60 && ZvonOne.charAt(2) == ':' && ZvonTwoOne < 25 && ZvonTwoTwo < 60 && ZvonTwo.charAt(2) == ':') {
                                                if ((ZvonOneOne < ZvonTwoOne) || (ZvonOneOne == ZvonTwoOne && ZvonOneTwo < ZvonTwoTwo)) {
                                                    StringBuffer stringBuffer = new StringBuffer();
                                                    try {
                                                        boolean Zapic = true;

                                                        StringBuffer stringBuffered = new StringBuffer();

                                                        try {
                                                            FileInputStream read = getActivity().openFileInput("Friday.txt");
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

                                                        String[] mas = stringBuffered.toString().split("~");
                                                        for (int i = 0; i < mas.length; i++) {
                                                            String[] helping ;
                                                            helping = mas[i].split("=");
                                                            if((Integer.parseInt(helping[0].substring(0,2)) == ZvonOneOne && Integer.parseInt(helping[0].substring(3,5)) == ZvonOneTwo) || (Integer.parseInt(helping[0].substring(8,10)) == ZvonTwoOne && Integer.parseInt(helping[0].substring(11)) == ZvonTwoTwo)) {
                                                                throw new Povtor("Syko blyat", 1);
                                                            }
                                                            if(Integer.parseInt(helping[0].substring(0,2)) > ZvonOneOne   && Zapic) {
                                                                stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", Кабинет №" + NumKab).append(("\n")).append(mas[i]).append(("\n"));
                                                                Zapic = false;

                                                            } else
                                                                stringBuffer.append(mas[i]).append(("\n"));
                                                        }
                                                        if (Zapic)
                                                            stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", Кабинет №" + NumKab);
                                                        try {
                                                            FileOutputStream write =  getActivity().openFileOutput("Friday.txt", getActivity().MODE_PRIVATE);
                                                            String temp_write = stringBuffer.toString();

                                                            write.write(temp_write.getBytes());
                                                            write.close();
                                                        } catch (FileNotFoundException e) {
                                                            e.printStackTrace();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }

                                                        Start();
                                                        lvMain = viewGroup.findViewById(R.id.lvFriday);
                                                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                                                                new String[]{"Times", "Kab"},
                                                                new int[]{R.id.textView1, R.id.textView1_2});
                                                        lvMain.setAdapter(adapter);

                                                    } catch (Povtor povtor) {
                                                        Toast.makeText(getActivity(),"Временной промежуток не должен совпадать с предыдущем или начинаться позже, а заканчиваться раньше",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                else
                                                    Toast.makeText(getActivity(), "Не верный промежуток! Первое значение не может быть больше второго!", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(
                                                        getActivity(), "Все поля должны быть заполненны!", Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(
                                                    getActivity(), "Не верный формат!", Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = newzvonok.create();

                //и отображаем его:
                // alertDialog.setTitle("Новый урок");
                alertDialog.show();


                return true;
            }
        });
        return viewGroup;
    }

    public boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void Start() {
        String[] help ;
        String delimeter = "=";
        products.clear();
        try {
            FileInputStream read = getActivity().openFileInput("Friday.txt");
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

    }



}