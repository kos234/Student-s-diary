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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import kos.progs.diary.Constant;
import kos.progs.diary.constructors.ConstructorDnewnik;
import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.adapters.AdapterPagerInCard;
import kos.progs.diary.onBackPressed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentDnewnik extends Fragment implements onBackPressed {
    private Context context;
    private TextView dateNedel;
    private ArrayList<ConstructorDnewnik> ConstructorDnewniks = new ArrayList<>();
    public int startNedeli;
    public int startMes;
    public int dayInMes;
    public int endMes;
    public int endNedeli;
    public int year = Calendar.getInstance().get(Calendar.YEAR);
    public String nameMes;
    public AdapterPagerInCard adapterPagerInCard;
    private ViewPager viewPager;
    private SharedPreferences Current_Theme;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private RelativeLayout view;
    public AlertDialog alertDialog = null;
    private int[] scrolls = new int[]{0, 0, 0, 0, 0, 0};
    private String[] ids = new String[]{"-1"};
    public String[] currentWindow = new String[]{"null"};
    private String url, nameDay, ulrTwo;
    private final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        try {
            Bundle b;
            if (savedInstanceState == null)
                b = getArguments();
            else
                b = savedInstanceState;
            view = (RelativeLayout) inflater.inflate(R.layout.fragment_dnewnik, container, false);
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(0, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 0).apply();
            } catch (NullPointerException ignored) {
            }
            dateNedel = view.findViewById(R.id.textViewDnew);
            if (Objects.requireNonNull(b).size() != 0) {
                ConstructorDnewniks = b.getParcelableArrayList("list");

                if (Objects.requireNonNull(ConstructorDnewniks).size() == 6 && !settings.getBoolean("SaturdaySettings", true))
                    ConstructorDnewniks.remove(5);
                else if (ConstructorDnewniks.size() == 5 && settings.getBoolean("SaturdaySettings", true))
                    onGenerate(5);
                scrolls = b.getIntArray("scrolls");
                currentWindow = b.getStringArray("currentWindow");
                if (Objects.requireNonNull(currentWindow)[0].equals("alertClear")) {
                    alertClear();
                } else if (currentWindow[0].equals("edit")) {
                    ids = currentWindow;
                }
                onPostClear();
                ids = new String[]{"-1"};

                viewPager.setCurrentItem(b.getInt("currentItem"));
                dateNedel.setText(b.getString("dateNedel"));

                nameMes = b.getString("nameMes");
                startMes = b.getInt("startMes");
                startNedeli = b.getInt("startNedeli");
                endMes = b.getInt("endMes");
                endNedeli = b.getInt("endNedeli");
                year = b.getInt("year");
            } else
                new StartAsyncTask().execute();

            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.textViewDnew);
            ((MainActivity) context).toolbar.setTitle(getString(R.string.app_name));
            Cliks(view);

            dateNedel.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            dateNedel.setOnClickListener(view -> {
                try {
                    alertClear();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
        return view;
    }

    public void alertClear() {
        try {
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            final AlertDialog.Builder deleted = new AlertDialog.Builder(context);
            currentWindow = new String[]{"alertClear"};
            deleted.setView(promptsView);
            deleted.setCancelable(true);
            alertDialog = deleted.create();
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

            final TextView ButtonClearAll = promptsView.findViewById(R.id.button_three_alert);
            ButtonClearAll.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonClearAll.setOnClickListener(view -> {
                try {
                    new ClearAllAsyncTask().execute();
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            promptsView.findViewById(R.id.viewBorderTwo).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            TextView ButtonClearHomework = promptsView.findViewById(R.id.button_two_alert);
            ButtonClearHomework.setOnClickListener(view -> {
                try {
                    new ClearDzAsyncTask().execute();
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonClearHomework.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

            TextView ButtonClearOcenki = promptsView.findViewById(R.id.button_one_alert);
            ButtonClearOcenki.setOnClickListener(view -> {
                try {
                    new ClearOcenkiAsyncTask().execute();
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonClearOcenki.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

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
    public void onPause() {
        super.onPause();
        try {
            if (alertDialog != null)
                alertDialog.dismiss();
            Bundle bundle = ((MainActivity) context).getBundles.get(0);
            bundle.putParcelableArrayList("list", ConstructorDnewniks);
            bundle.putInt("startNedeli", startNedeli);
            bundle.putInt("startMes", startMes);
            bundle.putInt("year", year);
            bundle.putInt("endNedeli", endNedeli);
            bundle.putInt("endMes", endMes);
            bundle.putString("nameMes", nameMes);
            try {
                bundle.putString("dateNedel", dateNedel.getText().toString());
            } catch (Exception e) {
                bundle.putIntArray("scrolls", scrolls);
            }
            try {
                bundle.putIntArray("scrolls", adapterPagerInCard.getScrolls());
            } catch (Exception e) {
                bundle.putInt("currentItem", 0);
            }
            try {
                bundle.putInt("currentItem", viewPager.getCurrentItem());
            } catch (Exception e) {
                bundle.putString("dateNedel", "");
            }
            bundle.putStringArray("currentWindow", currentWindow);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            outState.putParcelableArrayList("list", ConstructorDnewniks);
            outState.putInt("startNedeli", startNedeli);
            outState.putInt("startMes", startMes);
            outState.putInt("year", year);
            outState.putInt("endNedeli", endNedeli);
            outState.putInt("endMes", endMes);
            outState.putString("nameMes", nameMes);
            try {
                outState.putString("dateNedel", dateNedel.getText().toString());
            } catch (Exception e) {
                outState.putIntArray("scrolls", scrolls);
            }
            try {
                outState.putIntArray("scrolls", adapterPagerInCard.getScrolls());
            } catch (Exception e) {
                outState.putInt("currentItem", 0);
            }
            try {
                outState.putInt("currentItem", viewPager.getCurrentItem());
            } catch (Exception e) {
                outState.putString("dateNedel", "");
            }
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

    private void Cliks(final View viewRoditel) {
        try {
            ImageButton imageButtonOne = viewRoditel.findViewById(R.id.imageButtonDnew);
            imageButtonOne.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            ImageButton imageButtonTwo = viewRoditel.findViewById(R.id.imageButtonDnew2);
            imageButtonTwo.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            imageButtonOne.setOnClickListener(view -> {
                try {
                    new LeftAsyncTask().execute();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            imageButtonTwo.setOnClickListener(view -> {
                try {
                    new RightAsyncTask().execute();
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

    @Override
    public boolean onBackPressed() {
        try {
            if (alertDialog != null) {
                alertDialog.hide();
                alertDialog = null;
                return true;
            }
            return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    class ClearAllAsyncTask extends AsyncTask<Void, String[], Void> {

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
                onPostClear();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConstructorDnewniks.clear();
                for (int i = 0; i < 6; i++) {
                    if (i + 1 == 6 && !settings.getBoolean("SaturdaySettings", true))
                        continue;

                    url = (startNedeli + i) + "." + startMes + "." + year;

                    switch (i) {
                        case 1:
                            ulrTwo = "Tuesday.txt";
                            nameDay = context.getString(R.string.tuesday);
                            break;
                        case 2:
                            ulrTwo = "Wednesday.txt";
                            nameDay = context.getString(R.string.wednesday);
                            break;
                        case 3:
                            ulrTwo = "Thursday.txt";
                            nameDay = context.getString(R.string.thursday);
                            break;
                        case 4:
                            ulrTwo = "Friday.txt";
                            nameDay = context.getString(R.string.friday);
                            break;
                        case 5:
                            ulrTwo = "Saturday.txt";
                            nameDay = context.getString(R.string.saturday);
                            break;

                        default:
                            ulrTwo = "Monday.txt";
                            nameDay = context.getString(R.string.monday);
                            break;
                    }
                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read = context.openFileInput(ulrTwo);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        String[] help;
                        boolean isFileNotFound = true;
                        List<String> strings = new ArrayList<>();
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            isFileNotFound = false;
                            help = temp_read.split("=");
                            stringBuffer.append(help[1]).append("=").append(help[2]).append("= = ").append("=\n");
                            strings.add(help[1] + "=" + help[2] + "= = ");
                        }
                        if (isFileNotFound)
                            throw new FileNotFoundException();

                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, strings, false));
                    } catch (FileNotFoundException q) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add(getString(R.string.nullTimetablesName) + "=" + getString(R.string.nullTimetablesKab) + "=" + getString(R.string.nullTimetablesDz) + "= ");
                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, arrayList, true));
                    } catch (IOException j) {
                        j.printStackTrace();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
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
                    } catch (FileNotFoundException ignored) {
                    } catch (IOException a) {
                        a.printStackTrace();
                    }


                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public void onPost() {
        try {
            onPostClear();

            String[] textUrl = new String[4];
            if (startNedeli < 10) textUrl[0] = "0" + startNedeli;
            else textUrl[0] = Integer.toString(startNedeli);
            if (startMes < 10) textUrl[1] = "0" + startMes;
            else textUrl[1] = Integer.toString(startMes);
            if (endNedeli < 10) textUrl[2] = "0" + endNedeli;
            else textUrl[2] = Integer.toString(endNedeli);
            if (endMes < 10) textUrl[3] = "0" + endMes;
            else textUrl[3] = Integer.toString(endMes);
            dateNedel.setText(textUrl[0] + "." + textUrl[1] + " - " + textUrl[2] + "." + textUrl[3]);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onPostClear() {
        try {
            adapterPagerInCard = new AdapterPagerInCard(ConstructorDnewniks, context, scrolls, ids);
            view.removeView(view.findViewById(R.id.ProgressBar));
            viewPager = new ViewPager(context);
            viewPager.setAdapter(adapterPagerInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(settings.getInt("dpSizeSettings", 10) * MainActivity.dpSize, 0, settings.getInt("dpSizeSettings", 10) * MainActivity.dpSize, 0);
            viewPager.setPageMargin(60);
            viewPager.setId(R.id.ContentFragment);
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
            view.addView(viewPager, layoutParams);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
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

    class LeftAsyncTask extends AsyncTask<Void, String[], Void> {
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
                startMes = 0;
                dayInMes = 0;
                endMes = 0;
                endNedeli = 0;
                ConstructorDnewniks.clear();
                switch (nameMes) {
                    case "Jan":
                        startMes = 1;
                        endMes = 12;
                        dayInMes = new Constant().Dec;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 12;
                            endMes = 1;
                            editor.putString("StartMes", "Dec");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Feb":
                        startMes = 2;
                        endMes = 1;
                        dayInMes = new Constant().Jan;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 1;
                            endMes = 2;
                            editor.putString("StartMes", "Jan");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Mar":
                        startMes = 3;
                        endMes = 2;
                        if (year % 4 == 0 && (year % 100 != 0) || (year % 400 == 0))
                            dayInMes = new Constant().FebVesok;
                        else
                            dayInMes = new Constant().Feb;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 2;
                            endMes = 3;
                            editor.putString("StartMes", "Feb");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Apr":
                        startMes = 4;
                        endMes = 3;
                        dayInMes = new Constant().Mar;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 3;
                            endMes = 4;
                            editor.putString("StartMes", "Mar");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "May":
                        startMes = 5;
                        endMes = 4;
                        dayInMes = new Constant().Apr;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 4;
                            endMes = 5;
                            editor.putString("StartMes", "Apr");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Jun":
                        startMes = 6;
                        endMes = 5;
                        dayInMes = new Constant().May;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 5;
                            endMes = 6;
                            editor.putString("StartMes", "May");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Jul":
                        startMes = 7;
                        endMes = 6;
                        dayInMes = new Constant().Jun;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 6;
                            endMes = 7;
                            editor.putString("StartMes", "Jun");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Aug":
                        startMes = 8;
                        endMes = 7;
                        dayInMes = new Constant().Jul;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 7;
                            endMes = 8;
                            editor.putString("StartMes", "Jul");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Sep":
                        startMes = 9;
                        endMes = 8;
                        dayInMes = new Constant().Aug;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 8;
                            endMes = 9;
                            editor.putString("StartMes", "Aug");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Oct":
                        startMes = 10;
                        endMes = 9;
                        dayInMes = new Constant().Sep;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 9;
                            endMes = 10;
                            editor.putString("StartMes", "Sep");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Nov":
                        startMes = 11;
                        endMes = 10;
                        dayInMes = new Constant().Oct;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 10;
                            endMes = 11;
                            editor.putString("StartMes", "Oct");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Dec":
                        startMes = 12;
                        endMes = 11;
                        dayInMes = new Constant().Nov;
                        startNedeli = startNedeli - 7;
                        if (startNedeli <= 0) {
                            startNedeli = dayInMes + startNedeli;
                            startMes = 11;
                            endMes = 12;
                            editor.putString("StartMes", "Nov");

                        }
                        if (startNedeli + 7 >= new Constant().Dec)
                            year--;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                }

                onReadFile();

            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class RightAsyncTask extends AsyncTask<Void, String[], Void> {
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
                startMes = 0;
                dayInMes = 0;
                endMes = 0;
                endNedeli = 0;
                ConstructorDnewniks.clear();
                switch (nameMes) {
                    case "Jan":
                        startMes = 1;
                        endMes = 2;
                        dayInMes = new Constant().Jan;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Feb");
                        }
                        if (startNedeli - 7 <= 0)
                            year++;
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Feb":
                        startMes = 2;
                        endMes = 3;

                        if (year % 4 == 0 && (year % 100 != 0) || (year % 400 == 0))
                            dayInMes = new Constant().FebVesok;
                        else
                            dayInMes = new Constant().Feb;

                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Mar");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Mar":
                        startMes = 3;
                        endMes = 4;
                        dayInMes = new Constant().Mar;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Apr");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Apr":
                        startMes = 4;
                        endMes = 5;
                        dayInMes = new Constant().Apr;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "May");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "May":
                        startMes = 5;
                        endMes = 6;
                        dayInMes = new Constant().May;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Jun");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Jun":
                        startMes = 6;
                        endMes = 7;
                        dayInMes = new Constant().Jun;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Jul");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Jul":
                        startMes = 7;
                        endMes = 8;
                        dayInMes = new Constant().Jul;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Aug");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Aug":
                        startMes = 8;
                        endMes = 9;
                        dayInMes = new Constant().Aug;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Sep");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Sep":
                        startMes = 9;
                        endMes = 10;
                        dayInMes = new Constant().Sep;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Oct");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Oct":
                        startMes = 10;
                        endMes = 11;
                        dayInMes = new Constant().Oct;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Nov");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;

                    case "Nov":
                        startMes = 11;
                        endMes = 12;
                        dayInMes = new Constant().Nov;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Dec");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                    case "Dec":
                        startMes = 12;
                        endMes = 1;
                        dayInMes = new Constant().Dec;
                        startNedeli = startNedeli + 7;
                        if (startNedeli > dayInMes) {
                            startNedeli = startNedeli - dayInMes;
                            startMes = endMes;
                            editor.putString("StartMes", "Jan");
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                }

                onReadFile();

            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class ClearOcenkiAsyncTask extends AsyncTask<Void, String[], Void> {

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
                onPostClear();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConstructorDnewniks.clear();

                for (int i = 0; i < 6; i++) {
                    if (i + 1 == 6 && !settings.getBoolean("SaturdaySettings", true))
                        continue;

                    url = (startNedeli + i) + "." + startMes + "." + year;

                    switch (i) {
                        case 1:
                            nameDay = context.getString(R.string.tuesday);
                            break;
                        case 2:
                            nameDay = context.getString(R.string.wednesday);
                            break;
                        case 3:
                            nameDay = context.getString(R.string.thursday);
                            break;
                        case 4:
                            nameDay = context.getString(R.string.friday);
                            break;
                        case 5:
                            nameDay = context.getString(R.string.saturday);
                            break;

                        default:
                            nameDay = context.getString(R.string.monday);
                            break;
                    }
                    StringBuilder stringBuffer = new StringBuilder();
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                    File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    if (!FileTxt.exists()) {
                        try {
                            FileTxt.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        FileInputStream read = new FileInputStream(FileTxt);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read;

                        String[] help;
                        boolean isFileNotFound = true;
                        List<String> strings = new ArrayList<>();
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            isFileNotFound = false;
                            help = temp_read.split("=");
                            stringBuffer.append(help[0]).append("=").append(help[1]).append("=").append(help[2]).append("= \n");
                            strings.add(help[0] + "=" + help[1] + "=" + help[2] + "= ");
                        }

                        if (isFileNotFound)
                            throw new FileNotFoundException();
                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, strings, false));

                    } catch (FileNotFoundException e) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add(getString(R.string.nullTimetablesName) + "=" + getString(R.string.nullTimetablesKab) + "=" + getString(R.string.nullTimetablesDz) + "= ");
                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, arrayList, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileOutputStream write = new FileOutputStream(FileTxt);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException ignored) {
                    } catch (IOException a) {
                        a.printStackTrace();
                    }
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class ClearDzAsyncTask extends AsyncTask<Void, String[], Void> {

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
                onPostClear();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ConstructorDnewniks.clear();
                for (int i = 0; i < 6; i++) {
                    if (i + 1 == 6 && !settings.getBoolean("SaturdaySettings", true))
                        continue;

                    url = (startNedeli + i) + "." + startMes + "." + year;

                    switch (i) {
                        case 1:
                            nameDay = context.getString(R.string.tuesday);
                            break;
                        case 2:
                            nameDay = context.getString(R.string.wednesday);
                            break;
                        case 3:
                            nameDay = context.getString(R.string.thursday);
                            break;
                        case 4:
                            nameDay = context.getString(R.string.friday);
                            break;
                        case 5:
                            nameDay = context.getString(R.string.saturday);
                            break;

                        default:
                            nameDay = context.getString(R.string.monday);
                            break;
                    }
                    StringBuilder stringBuffer = new StringBuilder();
                    File mFolder = new File(context.getFilesDir() + "/dnewnik");
                    File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    if (!FileTxt.exists()) {
                        try {
                            FileTxt.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        FileInputStream read = new FileInputStream(FileTxt);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read;
                        String[] help;
                        List<String> strings = new ArrayList<>();
                        boolean isFileNotFound = true;

                        while ((temp_read = bufferedReader.readLine()) != null) {
                            isFileNotFound = false;
                            help = temp_read.split("=");
                            stringBuffer.append(help[0]).append("=").append(help[1]).append("= =").append(help[3]).append("\n");
                            strings.add(help[0] + "=" + help[1] + "= =" + help[3]);
                        }
                        if (isFileNotFound)
                            throw new FileNotFoundException();
                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, strings, false));

                    } catch (FileNotFoundException e) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add(getString(R.string.nullTimetablesName) + "=" + getString(R.string.nullTimetablesKab) + "=" + getString(R.string.nullTimetablesDz) + "= ");
                        ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, arrayList, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileOutputStream write = new FileOutputStream(FileTxt);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (FileNotFoundException ignored) {
                    } catch (IOException a) {
                        a.printStackTrace();
                    }
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    class StartAsyncTask extends AsyncTask<Void, String[], Void> {

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

                Date start = new Date();
                switch (start.toString().substring(0, 3)) {
                    case "Tue":
                        viewPager.setCurrentItem(1);
                        break;
                    case "Wed":
                        viewPager.setCurrentItem(2);
                        break;
                    case "Thu":
                        viewPager.setCurrentItem(3);
                        break;
                    case "Fri":
                        viewPager.setCurrentItem(4);
                        break;
                    case "Sat":
                        viewPager.setCurrentItem(5);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Date date = new Date();
                startNedeli = Integer.parseInt(date.toString().substring(8, 10));
                nameMes = date.toString().substring(4, 7);
                startMes = 0;
                dayInMes = 0;
                endMes = 0;
                String dayName = date.toString().substring(0, 3);
                endNedeli = 0;
                int dayRemove = 0;
                switch (nameMes) {
                    case "Jan":
                        dayInMes = new Constant().Jan;
                        startMes = 1;
                        endMes = 2;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 12;
                            endMes = 1;
                            startNedeli = new Constant().Dec + startNedeli;
                            dayInMes = new Constant().Dec;
                            nameMes = "Dec";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Feb":
                        dayInMes = new Constant().Feb;
                        startMes = 2;
                        endMes = 3;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 1;
                            endMes = 2;
                            startNedeli = new Constant().Jan + startNedeli;
                            dayInMes = new Constant().Jan;
                            nameMes = "Jan";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Mar":
                        dayInMes = new Constant().Mar;
                        startMes = 3;
                        endMes = 4;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 2;
                            endMes = 3;
                            startNedeli = new Constant().Feb + startNedeli;
                            dayInMes = new Constant().Feb;
                            nameMes = "Feb";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Apr":
                        dayInMes = new Constant().Apr;
                        startMes = 4;
                        endMes = 5;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 3;
                            endMes = 4;
                            startNedeli = new Constant().Mar + startNedeli;
                            dayInMes = new Constant().Mar;
                            nameMes = "Mar";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "May":
                        dayInMes = new Constant().May;
                        startMes = 5;
                        endMes = 6;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 4;
                            endMes = 5;
                            startNedeli = new Constant().Apr + startNedeli;
                            dayInMes = new Constant().Apr;
                            nameMes = "Apr";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Jun":
                        dayInMes = new Constant().Jun;
                        startMes = 6;
                        endMes = 7;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 5;
                            endMes = 6;
                            startNedeli = new Constant().May + startNedeli;
                            dayInMes = new Constant().May;
                            nameMes = "May";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Jul":
                        dayInMes = new Constant().Jul;
                        startMes = 7;
                        endMes = 8;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 6;
                            endMes = 7;
                            startNedeli = new Constant().Jun + startNedeli;
                            dayInMes = new Constant().Jun;
                            nameMes = "Jun";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Aug":
                        dayInMes = new Constant().Aug;
                        startMes = 8;
                        endMes = 9;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 7;
                            endMes = 8;
                            startNedeli = new Constant().Jul + startNedeli;
                            dayInMes = new Constant().Jul;
                            nameMes = "Jul";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Sep":
                        dayInMes = new Constant().Sep;
                        startMes = 9;
                        endMes = 10;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 8;
                            endMes = 9;
                            startNedeli = new Constant().Aug + startNedeli;
                            dayInMes = new Constant().Aug;
                            nameMes = "Aug";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Oct":
                        dayInMes = new Constant().Oct;
                        startMes = 10;
                        endMes = 11;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 9;
                            endMes = 10;
                            startNedeli = new Constant().Sep + startNedeli;
                            dayInMes = new Constant().Sep;
                            nameMes = "Sep";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Nov":
                        dayInMes = new Constant().Nov;
                        startMes = 11;
                        endMes = 12;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 10;
                            endMes = 11;
                            startNedeli = new Constant().Oct + startNedeli;
                            dayInMes = new Constant().Oct;
                            nameMes = "Oct";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;

                        break;
                    case "Dec":
                        dayInMes = new Constant().Dec;
                        startMes = 12;
                        endMes = 1;
                        switch (dayName) {
                            case "Tue":
                                dayRemove = 1;
                                break;
                            case "Wed":
                                dayRemove = 2;
                                break;
                            case "Thu":
                                dayRemove = 3;
                                break;
                            case "Fri":
                                dayRemove = 4;
                                break;
                            case "Sat":
                                dayRemove = 5;
                                break;
                            case "Sun":
                                dayRemove = 6;
                                break;
                        }

                        startNedeli = startNedeli - dayRemove;
                        if (startNedeli <= 0) {
                            startMes = 11;
                            endMes = 12;
                            startNedeli = new Constant().Nov + startNedeli;
                            dayInMes = new Constant().Nov;
                            nameMes = "Nov";
                        }
                        endNedeli = startNedeli + 6;
                        if (endNedeli > dayInMes)
                            endNedeli = endNedeli - dayInMes;
                        else
                            endMes = startMes;
                        break;
                }

                onReadFile();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }


    }

    public void onReadFile() {
        try {
            for (int i = 0; i < 6; i++) {
                if (i + 1 == 6 && !settings.getBoolean("SaturdaySettings", true))
                    continue;
                onGenerate(i);
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onGenerate(int i) {
        try {
            url = (startNedeli + i) + "." + startMes + "." + year;

            switch (i) {
                case 1:
                    ulrTwo = "Tuesday.txt";
                    nameDay = context.getString(R.string.tuesday);
                    break;
                case 2:
                    ulrTwo = "Wednesday.txt";
                    nameDay = context.getString(R.string.wednesday);
                    break;
                case 3:
                    ulrTwo = "Thursday.txt";
                    nameDay = context.getString(R.string.thursday);
                    break;
                case 4:
                    ulrTwo = "Friday.txt";
                    nameDay = context.getString(R.string.friday);
                    break;
                case 5:
                    ulrTwo = "Saturday.txt";
                    nameDay = context.getString(R.string.saturday);
                    break;

                default:
                    ulrTwo = "Monday.txt";
                    nameDay = context.getString(R.string.monday);
                    break;
            }

            File mFolder = new File(context.getFilesDir() + "/dnewnik");
            File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            if (!FileTxt.exists()) {
                try {
                    FileTxt.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileInputStream read = new FileInputStream(FileTxt);
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String temp_read;
                List<String> strings = new ArrayList<>();

                boolean isFileNotFound = true;
                while ((temp_read = bufferedReader.readLine()) != null) {
                    isFileNotFound = false;

                    strings.add(temp_read);
                }
                if (isFileNotFound)
                    throw new FileNotFoundException();

                ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, strings, false));
            } catch (FileNotFoundException e) {
                StringBuilder stringBuffer = new StringBuilder();

                try {
                    FileInputStream read = context.openFileInput(ulrTwo);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    List<String> strings = new ArrayList<>();
                    String[] help;
                    boolean isFileNotFound = true;

                    while ((temp_read = bufferedReader.readLine()) != null) {
                        isFileNotFound = false;
                        help = temp_read.split("=");
                        stringBuffer.append(help[1]).append("=").append(help[2]).append("= = ").append("=\n");
                        strings.add(help[1] + "=" + help[2] + "= = ");
                    }

                    if (isFileNotFound)
                        throw new FileNotFoundException();

                    ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, strings, false));
                } catch (FileNotFoundException q) {
                    List<String> arrayList = new ArrayList<>();
                    arrayList.add(getString(R.string.nullTimetablesName) + "=" + getString(R.string.nullTimetablesKab) + "=" + getString(R.string.nullTimetablesDz) + "= ");
                    ConstructorDnewniks.add(new ConstructorDnewnik(nameDay, arrayList, true));
                } catch (IOException j) {
                    j.printStackTrace();
                }
                try {
                    FileOutputStream write = new FileOutputStream(FileTxt);
                    String temp_write = stringBuffer.toString();

                    write.write(temp_write.getBytes());
                    write.close();
                } catch (FileNotFoundException ignored) {
                } catch (IOException a) {
                    a.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }
}
