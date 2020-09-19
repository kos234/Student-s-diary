package kos.progs.diary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.SquareImageView;
import kos.progs.diary.onBackPressed;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentHelp extends Fragment implements onBackPressed {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Context context;
    private ScrollView scrollView;
    public String[] currentWindow = new String[]{"null"};
    private int scroll;

    @Override
    public void onPause() {
        super.onPause();
        try {
            Bundle bundle = ((MainActivity) context).getBundles.get(5);
            try {
                bundle.putInt("scroll", scrollView.getScrollY());
            } catch (Exception e) {
                bundle.putInt("scroll", scroll);
            }
            bundle.putStringArray("currentWindow", currentWindow);
            bottomSheetBehavior.removeBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public boolean onBackPressed() {
        try {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return true;
            } else
                return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            try {
                outState.putInt("scroll", scrollView.getScrollY());
            } catch (Exception e) {
                outState.putInt("scroll", scroll);
            }
            outState.putStringArray("currentWindow", currentWindow);
            super.onSaveInstanceState(outState);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_spravka, container, false);
        try {
            SharedPreferences Current_Theme = Objects.requireNonNull(context).getSharedPreferences("Current_Theme", MODE_PRIVATE);
            scrollView = view.findViewById(R.id.rootScrollHelp);
            Bundle b;
            final FrameLayout linearLayout = ((MainActivity) context).linearLayoutBottom;
            bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
            bottomSheetBehavior.addBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(5, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 5).apply();
            } catch (NullPointerException ignored) {
            }
            if (savedInstanceState == null)
                b = getArguments();
            else
                b = savedInstanceState;
            if (Objects.requireNonNull(b).size() != 0) {
                scroll = b.getInt("scroll");
                currentWindow = b.getStringArray("currentWindow");
            }
            scrollView.setScrollY(scroll);
            ((MainActivity) context).toolbar.setTitle(getString(R.string.help));
            TextView textViewObz = view.findViewById(R.id.obz_one);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_two);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_three);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_four);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_six);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_seven);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_eight);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_nine);
            textViewObz.setText(textViewObz.getText() + "\n\n" + Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/confirmations\n");
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_ten);
            textViewObz.setText(textViewObz.getText() + "\n\n" + Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/backups\n");
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_eleven);
            textViewObz.setText(textViewObz.getText() + "\n\n" + Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/errors\n");
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.obz_twelve);
            textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textViewObz = view.findViewById(R.id.button_donate);
            textViewObz.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            textViewObz = view.findViewById(R.id.button_error_act);
            textViewObz.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            linearLayout.removeAllViews();
            linearLayout.addView(LayoutInflater.from(context).inflate(R.layout.errors_log, null));
            textViewObz.setOnClickListener(v -> {
                try {
                    currentWindow = new String[]{"errors", String.valueOf(BottomSheetBehavior.STATE_EXPANDED)};
                    onClickError(linearLayout);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            if (currentWindow[0].equals("errors"))
                onClickError(linearLayout);
            linearLayout.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            SquareImageView imageButtonOne = linearLayout.findViewById(R.id.imageButtonErrorLeft);
            imageButtonOne.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            SquareImageView imageButtonTwo = linearLayout.findViewById(R.id.imageButtonErrorRight);
            imageButtonTwo.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);

            TextView textView = linearLayout.findViewById(R.id.textError);
            textView.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView = linearLayout.findViewById(R.id.textViewDate);
            textView.post(() -> bottomSheetBehavior.setPeekHeight(linearLayout.findViewById(R.id.textViewDate).getHeight() + 20 * MainActivity.dpSize));
            textView.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            FrameLayout linearLayout_padding = linearLayout.findViewById(R.id.field_create_fragment);
            linearLayout_padding.setPadding(0, ((MainActivity) context).getStatusBarSize(), 0, 0);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return view;
    }

    public void onClickError(FrameLayout linearLayout) {
        try {
            final File mFolder = new File(context.getExternalFilesDir(null) + "/errors");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            final File[] list = mFolder.listFiles();
            bottomSheetBehavior.addBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            try {
                bottomSheetBehavior.setState(Integer.parseInt(currentWindow[1]));
            } catch (IllegalArgumentException e) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            final TextView textViewDate = linearLayout.findViewById(R.id.textViewDate);
            final TextView textError = linearLayout.findViewById(R.id.textError);
            DisplayMetrics display = MainActivity.getResources.getDisplayMetrics();
            textError.setHeight(display.heightPixels);

            if (Objects.requireNonNull(list).length != 0) {
                textViewDate.setText(list[0].getName());
                StringBuilder text = new StringBuilder();
                try {
                    FileInputStream read = new FileInputStream(list[0]);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read;
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        text.append(temp_read).append("\n");
                    }
                } catch (IOException ignored) {
                }

                textError.setText(text.toString());

                SquareImageView imageButtonRight = linearLayout.findViewById(R.id.imageButtonErrorRight);
                textViewDate.setTag(0);
                imageButtonRight.setOnClickListener(view -> {
                    try {
                        int i = (int) textViewDate.getTag() + 1;
                        if (list.length > i && (int) textViewDate.getTag() >= 0) {
                            textViewDate.setText(list[i].getName());
                            StringBuilder text12 = new StringBuilder();
                            try {
                                FileInputStream read = new FileInputStream(list[i]);
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String temp_read;
                                while ((temp_read = bufferedReader.readLine()) != null) {
                                    text12.append(temp_read).append("\n");
                                }
                            } catch (IOException ignored) {
                            }

                            textError.setText(text12.toString());
                            textViewDate.setTag(i);
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                SquareImageView imageButtonLeft = linearLayout.findViewById(R.id.imageButtonErrorLeft);
                imageButtonLeft.setOnClickListener(view -> {
                    try {
                        int i = (int) textViewDate.getTag() - 1;
                        if (i >= 0 && (int) textViewDate.getTag() >= 0) {
                            textViewDate.setText(list[i].getName());
                            StringBuilder text1 = new StringBuilder();
                            try {
                                FileInputStream read = new FileInputStream(list[i]);
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String temp_read;
                                while ((temp_read = bufferedReader.readLine()) != null) {
                                    text1.append(temp_read).append("\n");
                                }
                            } catch (IOException ignored) {
                            }

                            textError.setText(text1.toString());
                            textViewDate.setTag(i);
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
            } else {
                textViewDate.setText(getString(R.string.Ok));
                textViewDate.setTag(-1);
                textError.setText(getString(R.string.errors_null));
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }


}