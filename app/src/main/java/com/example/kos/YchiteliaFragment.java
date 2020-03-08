package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
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
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class YchiteliaFragment extends Fragment {
    private Context context;
    private String NamePred, PredPred;
    private final ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList = new ArrayList<>();
    private RecyclerAdapter adapter;
    private SharedPreferences Current_Theme;
    private View viewFragment;
    private SharedPreferences settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            viewFragment = inflater.inflate(R.layout.fragment_ychitelia, container, false);
            settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);

            androidx.appcompat.widget.Toolbar toolbar = viewFragment.findViewById(R.id.toolbarPrepod);
            Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
            menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(menuToolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {try{
                    ((MainActivity) Objects.requireNonNull(getActivity())).openDrawer();
                }catch (Exception error){((MainActivity) context).errorStack(error);}  }
            });
            toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
            toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
            Start();
            RecyclerView recyclerView = viewFragment.findViewById(R.id.Ychitelia);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            adapter = new RecyclerAdapter(constrRecyclerViewArrayList, context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(final int position) {
                try{
                    final String textName = constrRecyclerViewArrayList.get(position).getTextName(),
                            textBottom = constrRecyclerViewArrayList.get(position).getTextBottom();

                    final LayoutInflater li = LayoutInflater.from(getActivity());
                    final View promptsView = li.inflate(R.layout.alert_delete_dnewnik, null);

                    AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                    deleted.setView(promptsView);
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                    Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if (settings.getBoolean("BorderAlertSettings", false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings", 4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                    promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);
                    final AlertDialog alertDialog = deleted.create();

                    TextView textTitle = promptsView.findViewById(R.id.title_alert);
                    textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                    textTitle.setText(context.getString(R.string.deleting));

                    TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                    textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    textBottomTitle.setText(context.getString(R.string.deleteTeacher));

                    TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                    ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    ButtonCancel.setText(getString(R.string.cancel));
                    ButtonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.hide();
                        }
                    });
                    promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
                    TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                    ButtonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            StringBuilder stringBuffer = new StringBuilder();

                            try {
                                FileInputStream read = Objects.requireNonNull(getActivity()).openFileInput("Ychitelia.txt");
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String temp_read;
                                while ((temp_read = bufferedReader.readLine()) != null) {

                                    if (!temp_read.equals(textName + "=" + textBottom))
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
                                FileOutputStream write = context.openFileOutput("Ychitelia.txt", context.MODE_PRIVATE);

                                write.write(stringBuffer.toString().getBytes());
                                write.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            constrRecyclerViewArrayList.remove(position);
                            if (settings.getBoolean("AnimationSettings", true))
                                adapter.notifyItemRemoved(position);
                            else
                                adapter.notifyDataSetChanged();
                            if (constrRecyclerViewArrayList.size() == 0)
                                TextViewVisible();

                            alertDialog.hide();
                        }
                    });
                    ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    ButtonSave.setText(getString(R.string.yes));

                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.show();
                }catch (Exception error){((MainActivity) context).errorStack(error);}}
            });

            adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final int position) {try{
                    final String textName = constrRecyclerViewArrayList.get(position).getTextName(),
                            textBottom = constrRecyclerViewArrayList.get(position).getTextBottom();

                    final LayoutInflater li = LayoutInflater.from(getActivity());
                    final View promptsView = li.inflate(R.layout.add_ychitelia, null);
                    AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                    newadd.setView(promptsView);
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                    Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if (settings.getBoolean("BorderAlertSettings", false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings", 4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                    promptsView.findViewById(R.id.alert_add_ychit).setBackground(alertbackground);

                    final AlertDialog alertDialog = newadd.create();

                    TextView textTitle = promptsView.findViewById(R.id.textView2);
                    textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));


                    TextView textBottomName = promptsView.findViewById(R.id.add_ychit_name);
                    textBottomName.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    TextView textBottomYrok = promptsView.findViewById(R.id.add_ychit_yrok);
                    textBottomYrok.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));


                    TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                    ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    ButtonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.hide();
                        }
                    });

                    final EditText name = promptsView.findViewById(R.id.NamePrepod);
                    final EditText predmet = promptsView.findViewById(R.id.yrokPrepod);
                    MainActivity.setCursorPointerColor(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                    MainActivity.setCursorPointerColor(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                    MainActivity.setCursorColor(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                    MainActivity.setCursorColor(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                    name.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                    name.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                    predmet.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                    predmet.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

                    name.setText(textName);
                    predmet.setText(textBottom);

                    TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                    ButtonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                StringBuilder stringBuffer = new StringBuilder();
                                NamePred = name.getText().toString();
                                PredPred = predmet.getText().toString();
                                if (NamePred.length() > 0 && PredPred.length() > 0) {
                                    try {
                                        FileInputStream read = Objects.requireNonNull(getActivity()).openFileInput("Ychitelia.txt");
                                        InputStreamReader reader = new InputStreamReader(read);
                                        BufferedReader bufferedReader = new BufferedReader(reader);
                                        String temp_read;
                                        while ((temp_read = bufferedReader.readLine()) != null) {
                                            if (!temp_read.equals(textName + "=" + textBottom)) {
                                                if (temp_read.equals(NamePred + "=" + PredPred))
                                                    throw new Povtor();

                                                stringBuffer.append(temp_read).append("\n");
                                            } else
                                                stringBuffer.append(NamePred).append("=").append(PredPred).append("\n");

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
                                        FileOutputStream write = context.openFileOutput("Ychitelia.txt", context.MODE_PRIVATE);
                                        String temp_write = stringBuffer.toString();

                                        write.write(temp_write.getBytes());
                                        write.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    constrRecyclerViewArrayList.get(position).changeText(NamePred, PredPred);
                                    adapter.notifyDataSetChanged();
                                    alertDialog.hide();

                                } else {
                                    Toast.makeText(context, context.getString(R.string.FieldsNot), Toast.LENGTH_LONG).show();
                                }

                            } catch (Povtor povtor) {
                                Toast.makeText(context, context.getString(R.string.warningPovtorYct), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.show();

                }catch (Exception error){((MainActivity) context).errorStack(error);}}
            });
            Button(viewFragment);
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}

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

    private void Start() {
        String[] help ;
        String delimeter = "=";
        constrRecyclerViewArrayList.clear();
        try {
            FileInputStream read = Objects.requireNonNull(getActivity()).openFileInput("Ychitelia.txt");
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
        TextView textView = viewFragment.findViewById(R.id.nullYchit);
        if(constrRecyclerViewArrayList.size() != 0){
            textView.setVisibility(View.INVISIBLE);
        }
        else
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

    }

    private void TextViewVisible() {
        TextView textView = viewFragment.findViewById(R.id.nullYchit);
        textView.setVisibility(View.VISIBLE);
        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
    }

    private void Button(final View viewFind){
        FloatingActionButton floatingActionButton = viewFind.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add))));
        Drawable drawableFAB = getResources().getDrawable(R.drawable.ic_add_24px);
        drawableFAB.setColorFilter(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)), PorterDuff.Mode.SRC_ATOP);
        floatingActionButton.setImageDrawable(drawableFAB);
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try{
                final LayoutInflater li = LayoutInflater.from(getActivity());
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
                textBottomTitle.setText(context.getString(R.string.deleteAllTeachers));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                    ButtonCancel.setText(getString(R.string.cancel));
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Deleted.hide();
                    }
                });
                    promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
                TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                    ButtonSave.setText(getString(R.string.yes));
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            FileOutputStream write =  Objects.requireNonNull(getActivity()).openFileOutput("Ychitelia.txt", context.MODE_PRIVATE);
                            String temp_write ="";

                            write.write(temp_write.getBytes());
                            write.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(settings.getBoolean("AnimationSettings",true))
                            new AnimationDel().execute();
                        else {
                            constrRecyclerViewArrayList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        Deleted.hide();

                    }
                });
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

                Objects.requireNonNull(Deleted.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Deleted.show();
                }catch (Exception error){((MainActivity) context).errorStack(error);}
                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_ychitelia , null);
                AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                newadd.setView(promptsView);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.alert_add_ychit).setBackground(alertbackground);
                final EditText name = promptsView.findViewById(R.id.NamePrepod);
                final EditText predmet =  promptsView.findViewById(R.id.yrokPrepod);
                MainActivity.setCursorPointerColor(name,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorPointerColor(predmet,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(name,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(predmet,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                final AlertDialog alertDialog = newadd.create();
                name.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                name.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                predmet.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                predmet.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));


                TextView textTitle = promptsView.findViewById(R.id.textView2);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));


                TextView textBottomName = promptsView.findViewById(R.id.add_ychit_name);
                textBottomName.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                TextView textBottomYrok = promptsView.findViewById(R.id.add_ychit_yrok);
                textBottomYrok.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
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
                        try {
                            StringBuilder stringBuffer = new StringBuilder();
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
                                    FileInputStream read = Objects.requireNonNull(getActivity()).openFileInput("Ychitelia.txt");
                                    InputStreamReader reader = new InputStreamReader(read);
                                    BufferedReader bufferedReader = new BufferedReader(reader);

                                    String temp_read;
                                    while ((temp_read = bufferedReader.readLine()) != null) {
                                        if (temp_read.equals(NamePred + "=" + PredPred))
                                            throw new Povtor();
                                        else
                                            stringBuffer.append(temp_read).append(("\n"));
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    FileOutputStream write = context.openFileOutput("Ychitelia.txt", context.MODE_PRIVATE);
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

                                TextView textView = viewFragment.findViewById(R.id.nullYchit);
                                textView.setVisibility(View.INVISIBLE);

                            } else {
                                Toast.makeText(context, context.getString(R.string.FieldsNot), Toast.LENGTH_LONG).show();
                            }
                            alertDialog.hide();
                        }  catch (Povtor povtor) {
                            Toast.makeText(context,context.getString(R.string.warningPovtorYct),Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                ButtonSave.setText(getString(R.string.save));

                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
                }catch (Exception error){((MainActivity) context).errorStack(error);} }
        });
    }

    public void notifyDeleted(){
        adapter.clearAll();
        TextViewVisible();
    }

    class AnimationDel extends AsyncTask<Void,Integer,Void>{
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try{
            adapter.notifyItemRemoved(values[0]);
            }catch (Exception error){((MainActivity) context).errorStack(error);}
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
            TextViewVisible();
            }catch (Exception error){((MainActivity) context).errorStack(error);}}

        @Override
        protected Void doInBackground(Void... voids) {
            try{
            for (int l = constrRecyclerViewArrayList.size() - 1; l >= 0; l--){
                constrRecyclerViewArrayList.remove(l);
                publishProgress(l);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            }catch (Exception error){((MainActivity) context).errorStack(error);}
            return null;
        }
    }

}