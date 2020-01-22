package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    View viewFragment;
    private SharedPreferences settings;

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (getActivity()!=null)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Fragment","Ychitelia" );
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragment =  inflater.inflate(R.layout.fragment_ychitelia, container,false);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);

        androidx.appcompat.widget.Toolbar toolbar = viewFragment.findViewById(R.id.toolbarPrepod);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        Start();
        recyclerView = viewFragment.findViewById(R.id.Ychitelia);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        adapter = new RecyclerAdapter(constrRecyclerViewArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {

                final String textName = constrRecyclerViewArrayList.get(position).getTextName(),
                        textBottom = constrRecyclerViewArrayList.get(position).getTextBottom();

                AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                deleted.setMessage(context.getString(R.string.deleteTeacher)).setCancelable(true).setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuffer stringBuffer = new StringBuffer();

                        try {
                            FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
                            InputStreamReader reader = new InputStreamReader(read);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            String temp_read;
                            while ((temp_read = bufferedReader.readLine()) != null) {

                                if  (!temp_read.equals(textName + "=" + textBottom))
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

                        constrRecyclerViewArrayList.remove(position);
                        if(settings.getBoolean("AnimationSettings",true))
                            adapter.notifyItemRemoved(position);
                        else
                            adapter.notifyDataSetChanged();
                        if(constrRecyclerViewArrayList.size() == 0)
                            TextViewVisible();


                    }
                })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = deleted.create();
                alertDialog.setTitle(context.getString(R.string.deleting));
                alertDialog.show();
            }
        });

       adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                final String textName = constrRecyclerViewArrayList.get(position).getTextName(),
                        textBottom = constrRecyclerViewArrayList.get(position).getTextBottom();

                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_ychitelia , null);
                AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                newadd.setView(promptsView);
                final EditText name = promptsView.findViewById(R.id.NamePrepod);
                final EditText predmet = promptsView.findViewById(R.id.yrokPrepod);
                name.setText(textName);
                predmet.setText(textBottom);
                newadd
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.save),
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
                                                while ((temp_read = bufferedReader.readLine()) != null) {
                                                    if  (!temp_read.equals(textName + "=" + textBottom))
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

                                            constrRecyclerViewArrayList.get(position).changeText(NamePred,PredPred);
                                            adapter.notifyDataSetChanged();

                                        }else {
                                            Toast.makeText(getActivity(),context.getString(R.string.FieldsNot),Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = newadd.create();

                //и отображаем его:

                alertDialog.show();
            }
        });
        Button(viewFragment);

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
        constrRecyclerViewArrayList.clear();
        try {
            FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
               help = temp_read.split(delimeter);
                constrRecyclerViewArrayList.add(new ConstrRecyclerView(help[0], help[1]));
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

        if(constrRecyclerViewArrayList.size() != 0){
            TextView textView = viewFragment.findViewById(R.id.nullYchit);
            textView.setVisibility(View.INVISIBLE);
        }

    }

    public void TextViewVisible() {
        TextView textView = viewFragment.findViewById(R.id.nullYchit);
        textView.setVisibility(View.VISIBLE);
    }

    public void Button (final View viewFind){
        FloatingActionButton floatingActionButton = viewFind.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setMessage(context.getString(R.string.deleteAllTeachers))
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                /*try {
                    FileOutputStream write =  getActivity().openFileOutput("Ychitelia.txt", getActivity().MODE_PRIVATE);
                    String temp_write ="";

                    write.write(temp_write.getBytes());
                    write.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } */
                        if(settings.getBoolean("AnimationSettings",true))
                            new AnimationDel().execute();
                        else {
                            constrRecyclerViewArrayList.clear();
                            adapter.notifyDataSetChanged();
                        }

                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });

                AlertDialog Deleted = Delete.create();
                Deleted.setTitle(context.getString(R.string.deleting));
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
                        .setPositiveButton(context.getString(R.string.save),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        try {
                                            StringBuffer stringBuffer = new StringBuffer();
                                            if (name.getText().toString().equals(""))
                                                NamePred = getString(R.string.fioExample);
                                            else
                                                NamePred = name.getText().toString();
                                            if (predmet.getText().toString().equals(""))
                                                PredPred = getString(R.string.lessonExample);
                                            else
                                                PredPred = predmet.getText().toString();

                                            if (NamePred.length() > 0 && PredPred.length() > 0) {
                                                try {
                                                    FileInputStream read = getActivity().openFileInput("Ychitelia.txt");
                                                    InputStreamReader reader = new InputStreamReader(read);
                                                    BufferedReader bufferedReader = new BufferedReader(reader);

                                                    String temp_read;
                                                    while ((temp_read = bufferedReader.readLine()) != null) {
                                                        if (temp_read.equals(NamePred + "=" + PredPred))
                                                            throw new Povtor("KRIA", 1);
                                                    else
                                                        stringBuffer.append(temp_read).append(("\n"));
                                                    }
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    FileOutputStream write = getActivity().openFileOutput("Ychitelia.txt", getActivity().MODE_PRIVATE);
                                                    String temp_write = stringBuffer.toString() + NamePred + "=" + PredPred;

                                                    write.write(temp_write.getBytes());
                                                    write.close();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                constrRecyclerViewArrayList.add(constrRecyclerViewArrayList.size(),new ConstrRecyclerView(NamePred, PredPred));
                                                if(settings.getBoolean("AnimationSettings",true))
                                                     adapter.notifyItemInserted(constrRecyclerViewArrayList.size() - 1);
                                                else
                                                    adapter.notifyDataSetChanged();

                                            } else {
                                                Toast.makeText(getActivity(), context.getString(R.string.FieldsNot), Toast.LENGTH_LONG).show();
                                            }
                                        }  catch (Povtor povtor) {
                                            Toast.makeText(context,context.getString(R.string.warningPovtorYct),Toast.LENGTH_LONG).show();
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

    class AnimationDel extends AsyncTask<Void,Integer,Void>{
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
            for (int l = constrRecyclerViewArrayList.size() - 1; l >= 0; l--){
                constrRecyclerViewArrayList.remove(l);
                publishProgress(l);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            return null;
        }
    }

}
