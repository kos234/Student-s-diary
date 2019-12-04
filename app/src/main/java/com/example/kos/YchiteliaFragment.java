package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class YchiteliaFragment extends Fragment {
    private Context context;
    private String NamePred, PredPred;
    private ArrayList<HashMap<String, String>> products = new ArrayList<>();
    private ListView listView;
    private SharedPreferences settings;

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (getActivity()!=null)) {
            settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Fragment","Ychitelia" );
            editor.apply();
            Toast.makeText(context,settings.getString("Fragment", "Dnewnik"),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewFragment =  inflater.inflate(R.layout.fragment_ychitelia, container,false);

        androidx.appcompat.widget.Toolbar toolbar = viewFragment.findViewById(R.id.toolbarPrepod);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        Button(viewFragment);
        Start();
        listView = viewFragment.findViewById(R.id.Ychitelia);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                new String[]{"Name", "Pred"},
                new int[]{R.id.textView1, R.id.textView1_2});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                final TextView textView = view.findViewById(R.id.textView1);
                AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setMessage("Удалить преподавателя?").setCancelable(true).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer stringBuffer = new StringBuffer();

                        try {
                            FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
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
                            FileOutputStream write = getActivity().openFileOutput("Ychitelia.txt",getActivity().MODE_PRIVATE);

                            write.write(stringBuffer.toString().getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Start();
                        listView = viewFragment.findViewById(R.id.Ychitelia);
                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                                new String[]{"Name", "Pred"},
                                new int[]{R.id.textView1,R.id.textView1_2});
                        listView.setAdapter(adapter);


                    }
                })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = deleted.create();
                alertDialog.setTitle("Удаление");
                alertDialog.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TextView textViewName = view.findViewById(R.id.textView1);
                final TextView textViewKab = view.findViewById(R.id.textView1_2);


                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_ychitelia , null);
                AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                newadd.setView(promptsView);
                final EditText name = (EditText) promptsView.findViewById(R.id.NamePrepod);
                final EditText predmet = (EditText) promptsView.findViewById(R.id.yrokPrepod);
                name.setText(textViewName.getText());
                predmet.setText(textViewKab.getText());
                newadd
                        .setCancelable(true)
                        .setPositiveButton("Добавить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        NamePred = name.getText().toString();
                                        PredPred = predmet.getText().toString();
                                        if (NamePred.length() > 0  && PredPred.length() > 0 ) {
                                            try {
                                                FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
                                                InputStreamReader reader = new InputStreamReader(read);
                                                BufferedReader bufferedReader = new BufferedReader(reader);
                                                String temp_read;
                                                String[] help ;
                                                String delimeter = "=";
                                                while ((temp_read = bufferedReader.readLine()) != null) {

                                                    help = temp_read.split(delimeter);


                                                    if  (!help[0].equals(textViewName.getText()))
                                                        stringBuffer.append(temp_read).append("\n");
                                                    else {
                                                        stringBuffer.append(NamePred + "="+ PredPred).append("\n");
                                                    }
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
                                                FileOutputStream write =  getActivity().openFileOutput("Ychitelia.txt", getActivity().MODE_PRIVATE);
                                                String temp_write = stringBuffer.toString();

                                                write.write(temp_write.getBytes());
                                                write.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Start();
                                            listView = viewFragment.findViewById(R.id.Ychitelia);
                                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                                                    new String[]{"Name", "Pred"},
                                                    new int[]{R.id.textView1,R.id.textView1_2});
                                            listView.setAdapter(adapter);
                                        }else {
                                            Toast.makeText(getActivity(),"Поле не должно быть пустым!",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = newadd.create();

                //и отображаем его:

                alertDialog.show();
                return true;
            }
        });
        return viewFragment;
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

    public void Start() {
        String[] help ;
        String delimeter = "=";
        products.clear();
        try {
            FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
               help = temp_read.split(delimeter);
                HashMap<String, String> map = new HashMap<>();
                map.put("Name",help[0] );
                map.put("Pred",help[1] );
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

    public void Button (final View viewFind){
        FloatingActionButton floatingActionButton = viewFind.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setMessage("Удалить всех учителей?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    FileOutputStream write =  getActivity().openFileOutput("Ychitelia.txt", getActivity().MODE_PRIVATE);
                    String temp_write ="";

                    write.write(temp_write.getBytes());
                    write.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Start();
                listView = viewFind.findViewById(R.id.Ychitelia);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                        new String[]{"Name", "Pred"},
                        new int[]{R.id.textView1,R.id.textView1_2});
                listView.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });

                AlertDialog Deleted = Delete.create();
                Deleted.setTitle("Удаление");
                Deleted.show();
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_ychitelia , null);
                AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                newadd.setView(promptsView);
                final EditText name = (EditText) promptsView.findViewById(R.id.NamePrepod);
                final EditText predmet = (EditText) promptsView.findViewById(R.id.yrokPrepod);
                newadd
                        .setCancelable(true)
                        .setPositiveButton("Добавить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        NamePred = name.getText().toString();
                                        PredPred = predmet.getText().toString();
                                        if (NamePred.length() > 0  && PredPred.length() > 0 ) {
                                        try {
                                            FileInputStream read =  getActivity().openFileInput("Ychitelia.txt");
                                            InputStreamReader reader = new InputStreamReader(read);
                                            BufferedReader bufferedReader = new BufferedReader(reader);

                                            String temp_read;
                                            while ((temp_read = bufferedReader.readLine()) != null) {
                                                stringBuffer.append(temp_read).append(("\n"));
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            FileOutputStream write =  getActivity().openFileOutput("Ychitelia.txt", getActivity().MODE_PRIVATE);
                                            String temp_write = stringBuffer.toString()  + NamePred + "="+ PredPred;

                                            write.write(temp_write.getBytes());
                                            write.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Start();
                                        listView = viewFind.findViewById(R.id.Ychitelia);
                                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), products, R.layout.new_item,
                                                new String[]{"Name", "Pred"},
                                                new int[]{R.id.textView1,R.id.textView1_2});
                                        listView.setAdapter(adapter);
                                    }else {
                                            Toast.makeText(getActivity(),"Поле не должно быть пустым!",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = newadd.create();

                //и отображаем его:

                alertDialog.show();
            }
        });
    }


}
