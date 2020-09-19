package kos.progs.diary.fragments;

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
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kos.progs.diary.constructors.ConstructorRecyclerView;
import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.adapters.AdapterRecycler;
import kos.progs.diary.onBackPressed;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentTeachers extends Fragment implements onBackPressed {
    private Context context;
    private String NamePred, PredPred;
    private ArrayList<ConstructorRecyclerView> constructorRecyclerViewArrayList = new ArrayList<>();
    private AdapterRecycler adapter;
    private View viewFragment;
    private SharedPreferences settings, Current_Theme;
    private RecyclerView recyclerView;
    public String[] currentWindow = new String[]{"null"};
    public AlertDialog alertDialog = null;

    public String[] getCurrentWindow() {
        try {
            return currentWindow;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return new String[]{"null"};
        }
    }

    public void setCurrentWindow(String[] currentWindow) {
        try {
            this.currentWindow = currentWindow;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Bundle b;
            if (savedInstanceState == null)
                b = getArguments();
            else
                b = savedInstanceState;
            viewFragment = inflater.inflate(R.layout.fragment_ychitelia, container, false);
            settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            recyclerView = viewFragment.findViewById(R.id.Ychitelia);
            SharedPreferences.Editor editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(2, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 2).apply();
            } catch (NullPointerException ignored) {
            }
            if (Objects.requireNonNull(b).size() != 0) {
                recyclerView.setScrollY(b.getInt("scroll"));
                constructorRecyclerViewArrayList = b.getParcelableArrayList("list");
                if (Objects.requireNonNull(constructorRecyclerViewArrayList).size() != 0)
                    TextViewInvisible();
                else
                    TextViewVisible();

                currentWindow = b.getStringArray("currentWindow");

                if (!Objects.requireNonNull(currentWindow)[0].equals("null")) {
                    switch (currentWindow[0]) {
                        case "deleteAll":
                            onDelete();
                            break;
                        case "create":
                            onCreateItem();
                            break;
                        case "edit":
                            onEdit(Integer.parseInt(currentWindow[1]));
                            break;
                    }
                }
            } else {
                Start();
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            adapter = new AdapterRecycler(constructorRecyclerViewArrayList, context, 0);
            recyclerView.setAdapter(adapter);

            ((MainActivity) context).toolbar.setTitle(getString(R.string.teachers));

            adapter.setOnItemClickListener(position -> {
                try {
                    onEdit(position);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            }, position -> {
                try {
                    StringBuilder stringBuffer = new StringBuilder();
                    String textName = constructorRecyclerViewArrayList.get(position).TextName,
                            textBottom = constructorRecyclerViewArrayList.get(position).TextBottom;
                    try {
                        FileInputStream read = Objects.requireNonNull(context).openFileInput("Ychitelia.txt");
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        FileOutputStream write = context.openFileOutput("Ychitelia.txt", MODE_PRIVATE);

                        write.write(stringBuffer.toString().getBytes());
                        write.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    constructorRecyclerViewArrayList.remove(position);
                    if (settings.getBoolean("AnimationSettings", true))
                        adapter.notifyItemRemoved(position);
                    else
                        adapter.notifyDataSetChanged();
                    if (constructorRecyclerViewArrayList.size() == 0)
                        TextViewVisible();

                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            Button(viewFragment);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return viewFragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (alertDialog != null)
                alertDialog.dismiss();
            Bundle bundle = ((MainActivity) context).getBundles.get(2);
            try {
                bundle.putInt("scroll", recyclerView.getScrollY());
            } catch (NullPointerException e) {
                bundle.putInt("scroll", 0);
            }
            bundle.putParcelableArrayList("list", constructorRecyclerViewArrayList);
            bundle.putStringArray("currentWindow", currentWindow);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            try {
                outState.putInt("scroll", recyclerView.getScrollY());
            } catch (NullPointerException e) {
                outState.putInt("scroll", 0);
            }
            outState.putParcelableArrayList("list", constructorRecyclerViewArrayList);
            outState.putStringArray("currentWindow", currentWindow);
            super.onSaveInstanceState(outState);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    public void onDelete() {
        try {
            currentWindow = new String[]{"deleteAll"};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
            Delete.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

            alertDialog = Delete.create();

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.deleting));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottomTitle.setText(context.getString(R.string.deleteAllTeachers));

            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setText(getString(R.string.cancel));
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
            promptsView.findViewById(R.id.viewBorderTwo).setVisibility(View.GONE);
            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setText(getString(R.string.yes));
            ButtonSave.setOnClickListener(view -> {
                try {
                    try {
                        FileOutputStream write = Objects.requireNonNull(context).openFileOutput("Ychitelia.txt", MODE_PRIVATE);
                        String temp_write = "";

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (settings.getBoolean("AnimationSettings", true))
                        new AnimationDel().execute();
                    else {
                        constructorRecyclerViewArrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setOnCancelListener(dialog -> {
                try {
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onCreateItem() {
        try {
            currentWindow = new String[]{"create"};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.add_ychitelia, null);
            AlertDialog.Builder newadd = new AlertDialog.Builder(context);
            newadd.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_add_ychit).setBackground(alertbackground);
            final EditText name = promptsView.findViewById(R.id.NamePrepod);
            final EditText predmet = promptsView.findViewById(R.id.yrokPrepod);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColorsQ(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            alertDialog = newadd.create();
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

            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setText(getString(R.string.cancel));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(view -> {
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
                            FileInputStream read = Objects.requireNonNull(context).openFileInput("Ychitelia.txt");
                            InputStreamReader reader = new InputStreamReader(read);
                            BufferedReader bufferedReader = new BufferedReader(reader);

                            String temp_read;
                            while ((temp_read = bufferedReader.readLine()) != null) {
                                if (temp_read.equals(NamePred + "=" + PredPred))
                                    throw new SQLException("Povtor");
                                else
                                    stringBuffer.append(temp_read).append(("\n"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            FileOutputStream write = context.openFileOutput("Ychitelia.txt", MODE_PRIVATE);
                            String temp_write = stringBuffer.toString() + NamePred + "=" + PredPred;

                            write.write(temp_write.getBytes());
                            write.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        constructorRecyclerViewArrayList.add(constructorRecyclerViewArrayList.size(), new ConstructorRecyclerView(NamePred, PredPred));
                        if (settings.getBoolean("AnimationSettings", true))
                            adapter.notifyItemInserted(constructorRecyclerViewArrayList.size() - 1);
                        else
                            adapter.notifyDataSetChanged();

                        if (adapter.constructorRecyclerViewArrayList.size() == 0)
                            viewFragment.findViewById(R.id.nullYchit).setVisibility(View.INVISIBLE);

                        alertDialog.hide();
                        alertDialog = null;
                        currentWindow = new String[]{"null"};
                    } else {
                        MainActivity.ToastMakeText(context, context.getString(R.string.FieldsNot));
                    }
                } catch (SQLException povtor) {
                    MainActivity.ToastMakeText(context, context.getString(R.string.warningPovtorYct));
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setText(getString(R.string.save));

            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setOnCancelListener(dialog -> {
                try {
                    currentWindow = new String[]{"null"};
                    alertDialog = null;
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onEdit(final int position) {
        try {
            currentWindow = new String[]{"edit", String.valueOf(position)};
            final String textName = constructorRecyclerViewArrayList.get(position).TextName,
                    textBottom = constructorRecyclerViewArrayList.get(position).TextBottom;

            final View promptsView = LayoutInflater.from(context).inflate(R.layout.add_ychitelia, null);
            AlertDialog.Builder newadd = new AlertDialog.Builder(context);
            newadd.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_add_ychit).setBackground(alertbackground);

            alertDialog = newadd.create();

            TextView textTitle = promptsView.findViewById(R.id.textView2);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            promptsView.findViewById(R.id.viewBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            TextView textBottomName = promptsView.findViewById(R.id.add_ychit_name);
            textBottomName.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            TextView textBottomYrok = promptsView.findViewById(R.id.add_ychit_yrok);
            textBottomYrok.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));


            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            final EditText name = promptsView.findViewById(R.id.NamePrepod);
            final EditText predmet = promptsView.findViewById(R.id.yrokPrepod);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColorsQ(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(name, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(predmet, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            name.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            name.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            predmet.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            predmet.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            name.setText(textName);
            predmet.setText(textBottom);

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(view -> {
                try {
                    StringBuilder stringBuffer = new StringBuilder();
                    NamePred = name.getText().toString();
                    PredPred = predmet.getText().toString();
                    if (NamePred.length() > 0 && PredPred.length() > 0) {
                        try {
                            FileInputStream read = Objects.requireNonNull(context).openFileInput("Ychitelia.txt");
                            InputStreamReader reader = new InputStreamReader(read);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            String temp_read;
                            while ((temp_read = bufferedReader.readLine()) != null) {
                                if (!temp_read.equals(textName + "=" + textBottom)) {
                                    if (temp_read.equals(NamePred + "=" + PredPred))
                                        throw new SQLException("Povtor");

                                    stringBuffer.append(temp_read).append("\n");
                                } else
                                    stringBuffer.append(NamePred).append("=").append(PredPred).append("\n");

                            }

                            bufferedReader.close();
                            reader.close();
                            read.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        try {
                            FileOutputStream write = context.openFileOutput("Ychitelia.txt", MODE_PRIVATE);
                            String temp_write = stringBuffer.toString();

                            write.write(temp_write.getBytes());
                            write.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        constructorRecyclerViewArrayList.get(position).changeText(NamePred, PredPred);
                        adapter.notifyDataSetChanged();
                        alertDialog.hide();
                        alertDialog = null;
                        currentWindow = new String[]{"null"};

                    } else {
                        MainActivity.ToastMakeText(context, context.getString(R.string.FieldsNot));
                    }

                } catch (SQLException povtor) {
                    MainActivity.ToastMakeText(context, context.getString(R.string.warningPovtorYct));
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setOnCancelListener(dialog -> {
                try {
                    currentWindow = new String[]{"null"};
                    alertDialog = null;
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void Start() {
        try {
            String[] help;
            constructorRecyclerViewArrayList.clear();
            try {
                FileInputStream read = Objects.requireNonNull(context).openFileInput("Ychitelia.txt");
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp_read;
                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split("=");
                    constructorRecyclerViewArrayList.add(new ConstructorRecyclerView(help[0], help[1]));
                }
                bufferedReader.close();
                reader.close();
                read.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignore) {

            }
            TextView textView = viewFragment.findViewById(R.id.nullYchit);
            if (constructorRecyclerViewArrayList.size() != 0) {
                textView.setVisibility(View.INVISIBLE);
            } else
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void TextViewVisible() {
        try {
            TextView textView = viewFragment.findViewById(R.id.nullYchit);
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void TextViewInvisible() {
        try {
            TextView textView = viewFragment.findViewById(R.id.nullYchit);
            textView.setVisibility(View.INVISIBLE);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void Button(final View viewFind) {
        try {
            FloatingActionButton floatingActionButton = viewFind.findViewById(R.id.floatingActionButton2);
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add))));
            Drawable drawableFAB = MainActivity.getResources.getDrawable(R.drawable.ic_add_24px);
            drawableFAB.setColorFilter(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton.setImageDrawable(drawableFAB);
            floatingActionButton.setOnLongClickListener(view -> {
                try {
                    onDelete();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
                return false;
            });

            floatingActionButton.setOnClickListener(view -> {
                try {
                    onCreateItem();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public boolean onBackPressed() {
        try {
            if (alertDialog != null) {
                alertDialog.hide();
                alertDialog = null;
                return true;
            } else
                return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    class AnimationDel extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                adapter.notifyItemRemoved(values[0]);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                TextViewVisible();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int l = constructorRecyclerViewArrayList.size() - 1; l >= 0; l--) {
                    constructorRecyclerViewArrayList.remove(l);
                    publishProgress(l);
                    try {
                        Thread.sleep(100);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

}