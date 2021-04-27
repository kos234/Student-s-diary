package kos.progs.diary.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import kos.progs.diary.constructors.ConstructorGrades;
import kos.progs.diary.FixSetTag;
import kos.progs.diary.fragments.FragmentGrades;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AdapterGrades extends PagerAdapter {
    final ArrayList<ConstructorGrades> constructorGrades;
    final Context context;
    private final ArrayList<RecyclerView> scrollViewArrayList = new ArrayList<>();
    private final HashMap<Integer, TextView> textViews = new HashMap<>();
    final ArrayList<String> lesions;
    private final int[] scrolls;
    final SharedPreferences Current_Theme;
    final SharedPreferences Confirmations;
    final SharedPreferences settings;
    String url;
    public MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    TextView textViewConf;
    FragmentGrades fragmentGrades;
    private TextView timeCurrent;
    private SeekBar seekBar;

    public AdapterGrades(ArrayList<ConstructorGrades> constructorGrades, ArrayList<String> lesions, Context context, int[] scrolls) {
        this.constructorGrades = constructorGrades;
        this.context = context;
        this.scrolls = scrolls;
        this.lesions = lesions;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        Confirmations = context.getSharedPreferences("Confirmations", MODE_PRIVATE);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        try {
            return constructorGrades.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_view, container, false);
        try {
            TextView NameDay;
            ConstructorGrades grades = constructorGrades.get(position);
            final RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            if (scrolls.length > position)
                recyclerView.post(() -> {
                    try {
                        recyclerView.setScrollY(scrolls[position]);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
            fragmentGrades = (FragmentGrades) ((MainActivity) context).fragmentManager.getFragments().get(0);
            scrollViewArrayList.add(recyclerView);
            CardView cardView = view.findViewById(R.id.card_table);
            cardView.setCardBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));

            ArrayList<ConstructorGrades> constructorGradesAdapter = new ArrayList<>();
            for (int q = 0; q < lesions.size(); q++) {
                constructorGradesAdapter.add(new ConstructorGrades(grades.num, lesions.get(q), grades.grades.get(q)));
            }

            NameDay = view.findViewById(R.id.textViewNameDay);
            NameDay.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            NameDay.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            NameDay.setText(grades.title);
            TextView tapLesion = view.findViewById(R.id.card_tab_one);
            tapLesion.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            tapLesion.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            view.findViewById(R.id.card_tab_two).setVisibility(View.GONE);

            TextView tapGrades = view.findViewById(R.id.card_tab_three);
            tapGrades.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            tapGrades.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            LinearLayout tableLayout = view.findViewById(R.id.tableDnew);
            textViewConf = new TextView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textViewConf.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, MainActivity.dpSize, 0, 0);
            textViewConf.setGravity(Gravity.CENTER);
            textViewConf.setLayoutParams(layoutParams);
            textViewConf.setTextColor(NameDay.getTextColors());
            textViewConf.setTextSize(20);
            textViewConf.setPadding(5 * MainActivity.dpSize, 5 * MainActivity.dpSize, 5 * MainActivity.dpSize, 5 * MainActivity.dpSize);
            textViewConf.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            textViews.put(position, textViewConf);
            url = fragmentGrades.url;
            if (Objects.requireNonNull(Confirmations.getString("Quarter" + url + "_" + position, context.getString(R.string.Not_Confirmed))).equals(context.getString(R.string.Not_Confirmed))) {
                textViewConf.setText(context.getString(R.string.Not_Confirmed));
                if (!grades.icNotGrades)
                    textViewConf.setTextColor(Current_Theme.getInt("custom_not_confirmed", ContextCompat.getColor(context, R.color.custom_not_confirmed)));
            } else textViewConf.setText(context.getString(R.string.Confirmed));
            textViewConf.setOnClickListener(v -> {
                try {
                    onConfirmation(position);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            tableLayout.addView(textViewConf);

            AdapterGradesPager adapter = new AdapterGradesPager(constructorGradesAdapter, tapLesion, tapGrades, context, fragmentGrades);
            adapter.setOnItemClickListener(v -> {
                try {
                    if (Objects.requireNonNull(Confirmations.getString("Quarter" + url + "_" + position, context.getString(R.string.Not_Confirmed))).equals(context.getString(R.string.Not_Confirmed)))
                        onClickEdit((TextView) v, position);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            fragmentGrades = (FragmentGrades) ((MainActivity) context).fragmentManager.getFragments().get(0);
            if (!fragmentGrades.currentWindow[0].equals("null")) {
                if (fragmentGrades.currentWindow[0].equals("confirmation")  || fragmentGrades.currentWindow[0].equals("micro"))
                    if (Integer.parseInt(fragmentGrades.currentWindow[1]) == position)
                        onConfirmation(Integer.parseInt(fragmentGrades.currentWindow[1]));
            }
            container.addView(view, 0);
            textViewConf.post(() -> {
                try {
                    if(textViewConf.getHeight() == 0){
                        int lastHeight = recyclerView.getHeight();
                        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                        layoutParams1.height = 0;
                        recyclerView.setLayoutParams(layoutParams1);
                        textViewConf.post(() -> {
                            try {
                                    layoutParams1.height = lastHeight - textViewConf.getHeight();
                                    recyclerView.setLayoutParams(layoutParams1);
                            } catch (Exception error) {
                                ((MainActivity) context).errorStack(error);
                            }
                        });
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
        return view;
    }

    public int[] getScrolls() {
        int[] scrollInt = new int[scrollViewArrayList.size()];
        for (int i = 0; i < scrollViewArrayList.size(); i++) {
            scrollInt[i] = scrollViewArrayList.get(i).getScrollY();
        }

        return scrollInt;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void releasePlayer() {
        try {
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        try {
            return view.equals(object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    public void onConfirmationSet(String url, String fileUrl) {
        try {
            fragmentGrades.alertDialog.hide();
            fragmentGrades.alertDialog = null;
            TextView textView = textViews.get(Integer.parseInt(url.split("_")[1]));
            Objects.requireNonNull(textView).setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textView.setText(context.getString(R.string.Confirmed));
            SharedPreferences.Editor editor = Confirmations.edit();
            editor.putString("Quarter" + url, fileUrl);
            editor.apply();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onCheckPermission(boolean icMicro, GridLayout viewConfirm, int position) {
        try {
            if (icMicro) {
                int[] sizes = new int[]{};
                if (fragmentGrades.currentWindow.length != 2)
                    sizes = new int[]{Integer.parseInt(fragmentGrades.currentWindow[2]), Integer.parseInt(fragmentGrades.currentWindow[3])};

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) context).viewConfirmTemp = viewConfirm;
                    ((MainActivity) context).urlTemp = url + "_" + position;
                    ((MainActivity) context).tempSizes = sizes;
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, MainActivity.REQUEST_CODE_MICROPHONE_CONF);
                } else {
                    ((MainActivity) context).MicroConfirmation(viewConfirm, url + "_" + position, sizes);
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) context).urlTemp = url + "_" + position;
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MainActivity.REQUEST_CODE_CAMERA_CONF);
                } else {
                    ((MainActivity) context).CameraConfirmation(url + "_" + position);
                }
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onConfirmation(final int position) {
        try {
            final View viewConfirm;
            TextView view = textViews.get(position);
            if (Objects.requireNonNull(view).getText().equals(context.getString(R.string.Not_Confirmed))) {

                viewConfirm = LayoutInflater.from(context).inflate(R.layout.confirmation_add, null);
                AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (settings.getBoolean("BorderAlertSettings", false)) {
                    alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                }
                viewConfirm.findViewById(R.id.grid_conf_add).setBackground(alertbackground);
                ImageButton ConfirmationMicrophone = viewConfirm.findViewById(R.id.ConfirmationMicrophone);
                Drawable drawableMic = ContextCompat.getDrawable(context, R.drawable.ic_microphone);
                Objects.requireNonNull(drawableMic).setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
                ConfirmationMicrophone.setImageDrawable(drawableMic);
                GradientDrawable drawableOne = (GradientDrawable) MainActivity.getResources.getDrawable(R.drawable.shape);
                drawableOne.setColor(Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
                drawableOne.setStroke(4, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                ConfirmationMicrophone.setBackground(drawableOne);
                ConfirmationMicrophone.setOnClickListener(view16 -> {
                    try {
                        onCheckPermission(true, (GridLayout) viewConfirm, position);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                ImageButton ConfirmationCamera = viewConfirm.findViewById(R.id.ConfirmationCamera);
                Drawable drawableCamera = ContextCompat.getDrawable(context, R.drawable.ic_camera);
                Objects.requireNonNull(drawableCamera).setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
                ConfirmationCamera.setImageDrawable(drawableCamera);
                ConfirmationCamera.setBackground(drawableOne);
                ConfirmationCamera.setOnClickListener(view15 -> {
                    try {
                        onCheckPermission(false, (GridLayout) viewConfirm, position);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
                if (fragmentGrades.currentWindow[0].equals("micro")) {
                    onCheckPermission(true, (GridLayout) viewConfirm, position);
                }

                ConfirmationAlert.setView(viewConfirm);
                ConfirmationAlert
                        .setCancelable(true);

                TextView textView = viewConfirm.findViewById(R.id.TextMicro);
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textView = viewConfirm.findViewById(R.id.text_camera_conf);
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                fragmentGrades.alertDialog = ConfirmationAlert.create();

                Objects.requireNonNull(fragmentGrades.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                fragmentGrades.alertDialog.show();
                fragmentGrades.alertDialog.setOnCancelListener(dialog -> {
                    try {
                        fragmentGrades.currentWindow = new String[]{"null"};
                        fragmentGrades.alertDialog = null;
                        if (!((MainActivity) context).icMicro) {
                            ((MainActivity) context).icMicro = true;
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
            } else {
                File mFolder = new File(context.getExternalFilesDir(null) + "/confirmations");
                AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (settings.getBoolean("BorderAlertSettings", false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                ConfirmationAlert.setCancelable(true);
                final File file;

                String[] format = Objects.requireNonNull(Confirmations.getString("Quarter" + url + "_" + position, context.getString(R.string.Not_Confirmed))).split("\\.");
                file = new File(mFolder.getAbsolutePath() + "/" + Confirmations.getString("Quarter" + url + "_" + position, context.getString(R.string.Not_Confirmed)));
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (format.length == 2)
                    switch (format[1]) {
                        case "jpg":
                            viewConfirm = LayoutInflater.from(context).inflate(R.layout.confirmed_photo, null);
                            ConfirmationAlert.setView(viewConfirm);
                            viewConfirm.findViewById(R.id.liner_conf_photo).setBackground(alertbackground);

                            TextView textView = viewConfirm.findViewById(R.id.text_conf_photo);
                            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                            ImageView imageView = viewConfirm.findViewById(R.id.ConfirmedPhoto);
                            imageView.setImageURI(Uri.parse(file.toString()));
                            fragmentGrades.alertDialog = ConfirmationAlert.create();

                            TextView button = viewConfirm.findViewById(R.id.button_one_conf);
                            button.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                            button.setOnClickListener(view14 -> {
                                try {
                                    fragmentGrades.alertDialog.hide();
                                    fragmentGrades.alertDialog = null;
                                    fragmentGrades.currentWindow = new String[]{"null"};
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });

                            Objects.requireNonNull(fragmentGrades.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            fragmentGrades.alertDialog.show();
                            fragmentGrades.alertDialog.setOnCancelListener(dialog -> {
                                try {
                                    fragmentGrades.currentWindow = new String[]{"null"};
                                    fragmentGrades.alertDialog = null;
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });
                            break;

                        case "amr":
                            viewConfirm = LayoutInflater.from(context).inflate(R.layout.confirmed_voice, null);
                            ConfirmationAlert.setView(viewConfirm);
                            viewConfirm.findViewById(R.id.liner_conf_voice).setBackground(alertbackground);

                            TextView textViewVoice = viewConfirm.findViewById(R.id.textView_conf_voice);
                            textViewVoice.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                            final ImageButton imageButton = viewConfirm.findViewById(R.id.playVoicePlayer);
                            DisplayMetrics metricsB = MainActivity.getResources.getDisplayMetrics();
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
                            layoutParams.width = metricsB.widthPixels / 6;
                            layoutParams.height = metricsB.widthPixels / 6;
                            imageButton.setLayoutParams(layoutParams);
                            final Drawable drawableImagePlay = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black);
                            Objects.requireNonNull(drawableImagePlay).setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                            imageButton.setImageDrawable(drawableImagePlay);

                            try {
                                if (mediaPlayer != null) {
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(context, Uri.fromFile(file));
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            TextView timeEnd = viewConfirm.findViewById(R.id.TimeEndVoice);
                            timeEnd.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                            timeCurrent = viewConfirm.findViewById(R.id.TimeCurrentVoice);
                            timeCurrent.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                            seekBar = viewConfirm.findViewById(R.id.voiceRecordPlayer);
                            seekBar.setMax(mediaPlayer.getDuration());
                            seekBar.getProgressDrawable().setColorFilter(Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)), PorterDuff.Mode.MULTIPLY);
                            seekBar.getThumb().setColorFilter(Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)), PorterDuff.Mode.SRC_IN);

                            seekBar.setOnTouchListener((view1, motionEvent) -> {
                                try {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                                        handler.postDelayed(startPlayProgressUpdater, 0);
                                    else
                                        handler.removeCallbacks(startPlayProgressUpdater);
                                    mediaPlayer.seekTo(seekBar.getProgress());

                                    if (seekBar.getProgress() / 1000 < 60)
                                        if (seekBar.getProgress() / 1000 < 10)
                                            timeCurrent.setText("00:0" + seekBar.getProgress() / 1000);
                                        else
                                            timeCurrent.setText("00:" + seekBar.getProgress() / 1000);
                                    else if (seekBar.getProgress() / 1000 / 60 < 10)
                                        if (seekBar.getProgress() / 1000 % 60 < 10)
                                            timeCurrent.setText("0" + seekBar.getProgress() / 1000 / 60 + ":0" + seekBar.getProgress() / 1000);
                                        else
                                            timeCurrent.setText("0" + seekBar.getProgress() / 1000 / 60 + ":" + seekBar.getProgress() / 1000);
                                    else if (seekBar.getProgress() / 1000 % 60 < 10)
                                        timeCurrent.setText(seekBar.getProgress() / 1000 / 60 + ":0" + seekBar.getProgress() / 1000);
                                    else
                                        timeCurrent.setText(seekBar.getProgress() / 1000 / 60 + ":" + seekBar.getProgress() / 1000);
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                                return false;
                            });


                            imageButton.setOnClickListener(view13 -> {
                                try {
                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.pause();
                                        imageButton.setImageDrawable(drawableImagePlay);
                                    } else {
                                        mediaPlayer.start();
                                        handler.postDelayed(startPlayProgressUpdater, 0);
                                        Drawable drawableImagePause = ContextCompat.getDrawable(context, R.drawable.ic_pause_black);
                                        Objects.requireNonNull(drawableImagePause).setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                                        imageButton.setImageDrawable(drawableImagePause);
                                    }
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });

                            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                try {
                                    Drawable drawableImagePlay1 = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black);
                                    Objects.requireNonNull(drawableImagePlay1).setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                                    imageButton.setImageDrawable(drawableImagePlay1);
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });

                            if (mediaPlayer.getDuration() / 1000 < 60)
                                if (mediaPlayer.getDuration() / 1000 < 10)
                                    timeEnd.setText("00:0" + mediaPlayer.getDuration() / 1000);
                                else
                                    timeEnd.setText("00:" + mediaPlayer.getDuration() / 1000);
                            else if (mediaPlayer.getDuration() / 1000 / 60 < 10)
                                if (mediaPlayer.getDuration() / 1000 % 60 < 10)
                                    timeEnd.setText("0" + mediaPlayer.getDuration() / 1000 / 60 + ":0" + mediaPlayer.getDuration() / 1000);
                                else
                                    timeEnd.setText("0" + mediaPlayer.getDuration() / 1000 / 60 + ":" + mediaPlayer.getDuration() / 1000);
                            else if ((int) Math.floor(mediaPlayer.getDuration() / 1000 % 60) < 10)
                                timeEnd.setText(mediaPlayer.getDuration() / 1000 / 60 + ":0" + mediaPlayer.getDuration() / 1000);
                            else
                                timeEnd.setText(mediaPlayer.getDuration() / 1000 / 60 + ":" + mediaPlayer.getDuration() / 1000);

                            fragmentGrades.alertDialog = ConfirmationAlert.create();

                            TextView buttonConf = viewConfirm.findViewById(R.id.button_one_conf);
                            buttonConf.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                            buttonConf.setOnClickListener(view12 -> {
                                try {
                                    fragmentGrades.alertDialog.hide();
                                    fragmentGrades.alertDialog = null;
                                    fragmentGrades.currentWindow = new String[]{"null"};
                                    if (mediaPlayer != null) {
                                        try {
                                            mediaPlayer.release();
                                            mediaPlayer = null;
                                            handler.removeCallbacks(startPlayProgressUpdater);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });

                            fragmentGrades.alertDialog.setOnCancelListener(dialogInterface -> {
                                try {
                                    if (mediaPlayer != null) {
                                        try {
                                            mediaPlayer.release();
                                            mediaPlayer = null;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    fragmentGrades.alertDialog = null;
                                    fragmentGrades.currentWindow = new String[]{"null"};
                                } catch (Exception error) {
                                    ((MainActivity) context).errorStack(error);
                                }
                            });
                            Objects.requireNonNull(fragmentGrades.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            fragmentGrades.alertDialog.show();
                            break;

                        default:
                            MainActivity.ToastMakeText(context, context.getString(R.string.errorGrades));
                            break;
                    }
            }
            if (fragmentGrades.currentWindow[0].equals("null"))
                fragmentGrades.currentWindow = new String[]{"confirmation", String.valueOf(position)};
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

    }

    private final Runnable startPlayProgressUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                if ((int) Math.floor(mediaPlayer.getCurrentPosition() / 1000) < 60)
                    if ((int) Math.floor(mediaPlayer.getCurrentPosition() / 1000) < 10)
                        timeCurrent.setText("00:0" + mediaPlayer.getCurrentPosition() / 1000);
                    else
                        timeCurrent.setText("00:" + mediaPlayer.getCurrentPosition() / 1000);
                else if ((int) Math.floor(mediaPlayer.getCurrentPosition() / 1000 / 60) < 10)
                    if ((int) Math.floor(mediaPlayer.getCurrentPosition() / 1000 % 60) < 10)
                        timeCurrent.setText("0" + mediaPlayer.getCurrentPosition() / 1000 / 60 + ":0" + mediaPlayer.getCurrentPosition() / 1000);
                    else
                        timeCurrent.setText("0" + mediaPlayer.getCurrentPosition() / 1000 / 60 + ":" + mediaPlayer.getCurrentPosition() / 1000);
                else if ((int) Math.floor(mediaPlayer.getCurrentPosition() / 1000 % 60) < 10)
                    timeCurrent.setText(mediaPlayer.getCurrentPosition() / 1000 / 60 + ":0" + mediaPlayer.getCurrentPosition() / 1000);
                else
                    timeCurrent.setText(mediaPlayer.getCurrentPosition() / 1000 / 60 + ":" + mediaPlayer.getCurrentPosition() / 1000);

                handler.postDelayed(this, 10);

            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }
    };

    public void onClickEdit(final TextView view, final int position) {
        try {
            final FixSetTag tags = (FixSetTag) view.getTag();
            fragmentGrades.currentWindow = new String[]{"edit", String.valueOf(tags.getNUM_DAY()), String.valueOf(tags.getPOSITION_LIST())};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.input_edittext, null);
            final AlertDialog.Builder input = new AlertDialog.Builder(context);
            input.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.input_edittext_linear).setBackground(alertbackground);

            fragmentGrades.alertDialog = input.create();
            final EditText editText = promptsView.findViewById(R.id.input_edittext);
            editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            editText.setText(view.getText());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            TextView button = promptsView.findViewById(R.id.input_edittext_button);
            button.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            button.setOnClickListener(v -> {
                fragmentGrades.currentWindow = new String[]{"null"};
                view.setText(editText.getText());
                fragmentGrades.alertDialog.hide();
                fragmentGrades.alertDialog = null;
                Objects.requireNonNull(textViews.get(position)).setTextColor(Current_Theme.getInt("custom_not_confirmed", ContextCompat.getColor(context, R.color.custom_not_confirmed)));
                constructorGrades.get(position).icNotGrades = false;
                ArrayList<String> arrayList = constructorGrades.get(position).grades;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (i == (int) tags.getPOSITION_LIST())
                        arrayList.set(i, editText.getText().toString());
                    else
                        arrayList.set(i, arrayList.get(i));
                }
                constructorGrades.get(position).grades = arrayList;

                try {
                    File mFolder = new File(context.getFilesDir() + "/ocenki");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                    if (!FileTxt.exists()) {
                        FileTxt.createNewFile();
                    }
                    FileInputStream read = new FileInputStream(FileTxt);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String temp_read;
                    StringBuilder stringBuilder = new StringBuilder();
                    String[] help;

                    boolean isFileNotFound = true;
                    for (int i = 0; (temp_read = bufferedReader.readLine()) != null; i++) {
                        if (i == (int) tags.getPOSITION_LIST()) {
                            help = temp_read.split("=");
                            for (int l = 0; l < settings.getInt("countChet", 4) + 4; l++) {
                                if (l == (int) tags.getNUM_DAY()) {
                                    stringBuilder.append(editText.getText().toString()).append("=");
                                } else stringBuilder.append(help[l]).append("=");
                            }
                            stringBuilder.append("\n");
                        } else stringBuilder.append(temp_read).append("\n");

                        isFileNotFound = false;
                    }
                    if (isFileNotFound)
                        throw new FileNotFoundException();

                    try {
                        FileOutputStream write = new FileOutputStream(FileTxt);
                        String temp_write = stringBuilder.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (IOException p) {
                        p.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            Objects.requireNonNull(fragmentGrades.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            fragmentGrades.alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = fragmentGrades.alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            wmlp.y = 15 * MainActivity.dpSize;

            fragmentGrades.alertDialog.show();
            fragmentGrades.alertDialog.setOnCancelListener(dialog -> {
                fragmentGrades.currentWindow = new String[]{"null"};
                fragmentGrades.alertDialog = null;
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }
}
