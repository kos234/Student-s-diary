package kos.progs.diary.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import kos.progs.diary.constructors.ConstructorGrades;
import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.adapters.AdapterGrades;
import kos.progs.diary.adapters.AdapterSpinner;
import kos.progs.diary.onBackPressed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;

public class FragmentGrades extends Fragment implements onBackPressed {
    private Context context;
    private SharedPreferences settings, Confirmations, Current_Theme;
    private SharedPreferences.Editor editor;
    public String url;
    private TextView textViewDate;
    private RelativeLayout view;
    private ViewPager viewPager;
    public String[] currentWindow = new String[]{"null"};
    ArrayList<String> lesion = new ArrayList<>();
    ArrayList<ConstructorGrades> constructorGrades = new ArrayList<>();
    private int[] scrolls = new int[]{0, 0, 0, 0, 0, 0};
    public AdapterGrades adapterGrades = null;
    public AlertDialog alertDialog = null;
    final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    private String[] day;

    @Override
    public View onCreateView(LayoutInflater inflaterFrag, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RelativeLayout) inflaterFrag.inflate(R.layout.fragment_ocenki, container, false);
        try {
            Bundle b;
            if (savedInstanceState == null) {
                b = getArguments();
            } else {
                b = savedInstanceState;
            }
            day = MainActivity.getResources.getStringArray(R.array.DayTxt);
            settings = Objects.requireNonNull(context).getSharedPreferences("Settings", MODE_PRIVATE);
            Confirmations = context.getSharedPreferences("Confirmations", MODE_PRIVATE);
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(3, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 3).apply();
            } catch (NullPointerException ignored) {
            }
            ((MainActivity) context).toolbar.setTitle(getString(R.string.grades));
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.textViewOcen);
            textViewDate = view.findViewById(R.id.textViewOcen);
            textViewDate.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textViewDate.setOnClickListener(view -> {
                try {
                    onDelete();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            if (Objects.requireNonNull(b).size() != 0) {
                scrolls = b.getIntArray("scrolls");
                currentWindow = b.getStringArray("currentWindow");
                constructorGrades = b.getParcelableArrayList("list");
                url = b.getString("url");
                lesion = b.getStringArrayList("lesion");
                if (constructorGrades.size() == 0)
                    onCheck(view);

                adapterGrades = new AdapterGrades(constructorGrades, lesion, context, scrolls);
                switch (currentWindow[0]) {
                    case "firstStart":
                        onTask(view);
                        break;
                    case "delete":
                        onDelete();
                        break;
                    case "warning":
                        onWarning();
                        break;
                }
                onViewPagerCreate();
                onPost();
            } else
                onCheck(view);

            ImageButton imageButtonLeft = view.findViewById(R.id.imageButtonLeft);
            imageButtonLeft.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            imageButtonLeft.setOnClickListener(view -> {
                try {
                    new LeftAsyncTask().execute();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ImageButton imageButtonRight = view.findViewById(R.id.imageButtonRight);
            imageButtonRight.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            imageButtonRight.setOnClickListener(view -> {
                try {
                    new RightAsyncTask().execute();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return view;
    }

    private void onDelete() {
        try {
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            currentWindow = new String[]{"delete"};
            AlertDialog.Builder deleted = new AlertDialog.Builder(context);
            deleted.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);
            alertDialog = deleted.create();

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.deleting));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottomTitle.setText(context.getString(R.string.ReplaceOcenki));

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
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
            promptsView.findViewById(R.id.viewBorderTwo).setVisibility(View.GONE);
            promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(view -> {
                try {
                    if (Objects.requireNonNull(Confirmations.getString("Quarter" + url + "_" + viewPager.getCurrentItem(), context.getString(R.string.Not_Confirmed))).equals(context.getString(R.string.Not_Confirmed))) {
                        new ClearAsyncTask().execute();
                    } else
                        MainActivity.ToastMakeText(context, context.getString(R.string.you_have_already_confirmed_grades));
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setText(getString(R.string.yes));

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

    public void onCheck(View view) {
        try {
            if (settings.contains("mesStartOcenki") && settings.contains("countChet")) {
                new StartAsyncTask().execute();
            } else {
                onTask(view);
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onTask(View view) {
        try {
            final TextView textView = view.findViewById(R.id.nullGrades);
            onStartTask(textView);
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textView.setOnClickListener(v -> {
                try {
                    onStartTask(textView);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (alertDialog != null)
                alertDialog.dismiss();
            Bundle bundle = ((MainActivity) context).getBundles.get(3);
            bundle.putString("url", url);
            try {
                bundle.putIntArray("scrolls", adapterGrades.getScrolls());
            } catch (NullPointerException e) {
                bundle.putIntArray("scrolls", scrolls);
            }
            bundle.putStringArrayList("lesion", lesion);
            bundle.putParcelableArrayList("list", constructorGrades);
            bundle.putStringArray("currentWindow", currentWindow);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            outState.putString("url", url);
            try {
                outState.putIntArray("scrolls", adapterGrades.getScrolls());
            } catch (NullPointerException e) {
                outState.putIntArray("scrolls", scrolls);
            }
            outState.putStringArrayList("lesion", lesion);
            outState.putParcelableArrayList("list", constructorGrades);
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

    private void onStartTask(final TextView textView) {
        try {
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.mes_ocenki, null);
            currentWindow = new String[]{"firstStart"};
            AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
            deleted.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.liner_mes_ocenki).setBackground(alertbackground);
            alertDialog = deleted.create();

            TextView textTitle = promptsView.findViewById(R.id.title_mes_ocenki);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            textTitle = promptsView.findViewById(R.id.bottom_title_mes_ocenki);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textTitle = promptsView.findViewById(R.id.number_quarters);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            final EditText editText = promptsView.findViewById(R.id.number_quarters_edit);
            if (settings.contains("countChet"))
                editText.setText(settings.getInt("countChet", 4));

            editText.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            editText.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }

            promptsView.findViewById(R.id.viewBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            final Spinner spinner = promptsView.findViewById(R.id.mount_choose);
            List<String> choose = new ArrayList<>();
            String[] temp = MainActivity.getResources.getStringArray(R.array.months);
            for (int i = 0; i < temp.length; i++) {
                if (i + 1 != temp.length)
                    choose.add(temp[i]);
            }

            final AdapterSpinner adapterSpinner = new AdapterSpinner(context, choose, true);
            spinner.setAdapter(adapterSpinner);
            spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
            spinner.setSelection(settings.getInt("mesStartOcenki", 8));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        editor.putInt("mesStartOcenki", i);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
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

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(view -> {
                try {
                    if (!editText.getText().toString().equals("0")) {
                        new StartAsyncTask().execute();
                        alertDialog.hide();
                        alertDialog = null;
                        textView.setVisibility(View.GONE);
                        if (editText.getText().toString().equals("") || editText.getText().toString().equals(" "))
                            editor.putInt("countChet", 4);
                        else
                            editor.putInt("countChet", Integer.parseInt(editText.getText().toString()));
                        if (!settings.contains("mesStartOcenki"))
                            editor.putInt("mesStartOcenki", 8);
                        editor.apply();

                        onWarning();
                    } else
                        MainActivity.ToastMakeText(context, context.getString(R.string.number_quarters_warning));

                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
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

    private void onWarning() {
        try {
            currentWindow = new String[]{"warning"};
            final View promptsViewWarning = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);

            AlertDialog.Builder warning = new AlertDialog.Builder(getActivity());
            warning.setView(promptsViewWarning);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsViewWarning.findViewById(R.id.alert_delete).setBackground(alertbackground);
            alertDialog = warning.create();

            TextView textTitle = promptsViewWarning.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.Warning));

            TextView textBottomTitle = promptsViewWarning.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottomTitle.setText(context.getString(R.string.warningOcenki));

            promptsViewWarning.findViewById(R.id.button_one_alert).setVisibility(View.GONE);
            promptsViewWarning.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
            promptsViewWarning.findViewById(R.id.viewBorderOne).setVisibility(View.GONE);
            promptsViewWarning.findViewById(R.id.viewBorderTwo).setVisibility(View.GONE);


            TextView ButtonSave = promptsViewWarning.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(view -> {
                try {
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setText(getString(R.string.Ok));

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

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void onPre() {
        try {
            view.removeView(view.findViewById(R.id.ContentFragment));
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setId(R.id.ProgressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            view.addView(progressBar, layoutParams);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onPost() {
        try {
            view.removeView(view.findViewById(R.id.ProgressBar));
            view.addView(viewPager);
            textViewDate.setText(url);
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
            } else return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    class ClearAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                onPre();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                onPost();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                constructorGrades.clear();
                lesion.clear();
                StringBuilder stringBuffer = new StringBuilder();
                for (String s : day) {

                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(s)));

                        String temp_read;
                        String[] help;
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split("=");
                            if (lesion.indexOf(help[1]) < 0) {
                                lesion.add(help[1]);
                                stringBuffer.append(help[1]);
                                for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                                    stringBuffer.append("= ");
                                }
                                stringBuffer.append("\n");
                            }
                        }
                    } catch (IOException q) {
                        q.printStackTrace();
                    }

                }
                if (lesion.size() != 0) {
                    if (settings.getBoolean("PovedSettings", true))
                        lesion.add(getString(R.string.behavior));
                    stringBuffer.append(getString(R.string.behavior));
                    for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                        stringBuffer.append("= ");
                    }
                    stringBuffer.append("\n");

                    try {
                        File mFolder = new File(context.getFilesDir() + "/ocenki");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write = new FileOutputStream(FileTxt);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (IOException p) {
                        p.printStackTrace();
                    }
                } else
                    lesion.add(getString(R.string.nullTimetablesName));

                String title = "error?!";
                for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                    if (settings.getInt("countChet", 4) > i)
                        title = RimNumber.toRim(i + 1) + " " + context.getString(R.string.Quarter);
                    else if (settings.getInt("countChet", 4) == i)
                        if (settings.getBoolean("icgradesExamination", true))
                            title = context.getString(R.string.gradesExamination);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 1 == i)
                        if (settings.getBoolean("icgradesYear", true))
                            title = context.getString(R.string.gradesYear);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 2 == i)
                        if (settings.getBoolean("icgradesEnd", true))
                            title = context.getString(R.string.gradesEnd);
                        else continue;

                    ArrayList<String> gradesTemp = new ArrayList<>();
                    for (int l = 0; l < lesion.size(); l++) {
                        gradesTemp.add(" ");
                    }
                    constructorGrades.add(new ConstructorGrades(i + 1, gradesTemp, true, title));
                }
                adapterGrades = new AdapterGrades(constructorGrades, lesion, context, scrolls);
                onViewPagerCreate();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public void onViewPagerCreate() {
        try {
            viewPager = new ViewPager(context);
            viewPager.setId(R.id.ContentFragment);
            viewPager.setAdapter(adapterGrades);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings", 10) * MainActivity.dpSize, 0, settings.getInt("dpSizeSettings", 10) * MainActivity.dpSize, 0);
            viewPager.setPageMargin(60);
            viewPager.setLayoutParams(layoutParams);
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
            viewPager.setCurrentItem(settings.getInt("CurrQuarter", 0));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    try {
                        editor.putInt("CurrQuarter", position);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    class RightAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                onPre();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                onPost();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String[] years = url.split(" - ");
                url = (Integer.parseInt(years[0]) + 1) + " - " + (Integer.parseInt(years[1]) + 1);
                generate();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class LeftAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                onPre();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                onPost();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String[] years = url.split(" - ");
                url = (Integer.parseInt(years[0]) - 1) + " - " + (Integer.parseInt(years[1]) - 1);
                generate();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class StartAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                onPre();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                onPost();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                if (settings.getInt("mesStartOcenki", 8) <= calendar.get(Calendar.MONTH)) {
                    url = year + " - " + (year + 1);
                } else {
                    url = (year - 1) + " - " + year;
                }
                generate();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public void generate() {
        try {
            try {
                constructorGrades.clear();
                File mFolder = new File(context.getFilesDir() + "/ocenki");
                File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!FileTxt.exists()) {
                    FileTxt.createNewFile();
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(FileTxt)));
                String temp_read;
                boolean icUmply = true;

                lesion.clear();
                ArrayList<ArrayList<String>> grades = new ArrayList<>();
                while ((temp_read = bufferedReader.readLine()) != null) {
                    String[] help = temp_read.split("=");
                    lesion.add(help[0]);
                    ArrayList<String> gradesTemp = new ArrayList<>(Arrays.asList(help).subList(1, help.length));
                    grades.add(gradesTemp);
                    icUmply = false;
                }

                if (icUmply)
                    throw new FileNotFoundException();

                if (lesion.indexOf(getString(R.string.behavior)) != -1 && !settings.getBoolean("PovedSettings", true))
                    lesion.remove(getString(R.string.behavior));
                String title = "error?!";
                for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                    if (settings.getInt("countChet", 4) > i)
                        title = RimNumber.toRim(i + 1) + " " + context.getString(R.string.Quarter);
                    else if (settings.getInt("countChet", 4) == i)
                        if (settings.getBoolean("icgradesExamination", true))
                            title = context.getString(R.string.gradesExamination);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 1 == i)
                        if (settings.getBoolean("icgradesYear", true))
                            title = context.getString(R.string.gradesYear);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 2 == i)
                        if (settings.getBoolean("icgradesEnd", true))
                            title = context.getString(R.string.gradesEnd);
                        else continue;

                    ArrayList<String> gradesTemp = new ArrayList<>();
                    boolean icNotGrades = true;
                    for (int l = 0; l < lesion.size(); l++) {
                        String item = " ";
                        if (grades.get(l).size() > i)
                            item = grades.get(l).get(i);
                        icNotGrades = icNotGrades && (item.equals("") || item.equals(" "));
                        gradesTemp.add(item);
                    }
                    constructorGrades.add(new ConstructorGrades(i + 1, gradesTemp, icNotGrades, title));
                }
                adapterGrades = new AdapterGrades(constructorGrades, lesion, context, scrolls);

            } catch (FileNotFoundException e) {
                StringBuilder stringBuffer = new StringBuilder();
                for (String s : day) {

                    try {
                        FileInputStream read = context.openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help;
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split("=");
                            if (lesion.indexOf(help[1]) < 0) {
                                lesion.add(help[1]);
                                stringBuffer.append(help[1]);
                                for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                                    stringBuffer.append("= ");
                                }
                                stringBuffer.append("\n");
                            }
                        }
                    } catch (IOException q) {
                        q.printStackTrace();
                    }

                }
                if (lesion.size() != 0) {
                    if (settings.getBoolean("PovedSettings", true))
                        lesion.add(getString(R.string.behavior));

                    stringBuffer.append(getString(R.string.behavior));
                    for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                        stringBuffer.append("= ");
                    }
                    stringBuffer.append("\n");

                    try {
                        File mFolder = new File(context.getFilesDir() + "/ocenki");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write = new FileOutputStream(FileTxt);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (IOException p) {
                        p.printStackTrace();
                    }
                } else
                    lesion.add(getString(R.string.nullTimetablesName));

                String title = "error?!";
                for (int i = 0; i < settings.getInt("countChet", 4) + 3; i++) {
                    if (settings.getInt("countChet", 4) > i)
                        title = RimNumber.toRim(i + 1) + " " + context.getString(R.string.Quarter);
                    else if (settings.getInt("countChet", 4) == i)
                        if (settings.getBoolean("icgradesExamination", true))
                            title = context.getString(R.string.gradesExamination);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 1 == i)
                        if (settings.getBoolean("icgradesYear", true))
                            title = context.getString(R.string.gradesYear);
                        else continue;
                    else if (settings.getInt("countChet", 4) + 2 == i)
                        if (settings.getBoolean("icgradesEnd", true))
                            title = context.getString(R.string.gradesEnd);
                        else continue;

                    ArrayList<String> gradesTemp = new ArrayList<>();
                    for (int l = 0; l < lesion.size(); l++) {
                        gradesTemp.add(" ");
                    }
                    constructorGrades.add(new ConstructorGrades(i + 1, gradesTemp, true, title));
                }
                adapterGrades = new AdapterGrades(constructorGrades, lesion, context, scrolls);

            } catch (IOException e) {
                e.printStackTrace();
            }
            onViewPagerCreate();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public static class RimNumber {

        private static final TreeMap<Integer, String> map = new TreeMap<>();

        static {

            map.put(1000, "M");
            map.put(900, "CM");
            map.put(500, "D");
            map.put(400, "CD");
            map.put(100, "C");
            map.put(90, "XC");
            map.put(50, "L");
            map.put(40, "XL");
            map.put(10, "X");
            map.put(9, "IX");
            map.put(5, "V");
            map.put(4, "IV");
            map.put(1, "I");

        }

        public static String toRim(int number) {
            int l = map.floorKey(number);
            if (number == l) {
                return map.get(number);
            }
            return map.get(l) + toRim(number - l);
        }

    }
}