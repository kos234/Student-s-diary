package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class NastroikiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings,
            Current_Theme;
    public BottomSheetBehavior bottomSheetBehavior;
    public LinearLayout linearLayoutBottom;
    private SharedPreferences.Editor editor;
    private ArrayList<ConstrThemeRecycler> constrRecyclerViewArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerThemeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_nastroiki, container, false);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        editor = settings.edit();

        Toolbar toolbar = view.findViewById(R.id.toolbar4);
        Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
        menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(menuToolbar);        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));

        linearLayoutBottom = view.findViewById(R.id.field_create_fragment);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //Visual

        linearLayoutBottom.findViewById(R.id.field_create_fragment).setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        CardView cardVisual = view.findViewById(R.id.card_students);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_timetables);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_grades);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_settings);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_general_setting);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_themes);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_white_theme);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_dark_theme);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = view.findViewById(R.id.card_create_theme_nastroiki);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        cardVisual = linearLayoutBottom.findViewById(R.id.save_theme);
        cardVisual.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));

        TextView textVisual = view.findViewById(R.id.title_students);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_students);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.title_timetables);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_timetables_not);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_timetables_saturday);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.title_grades);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_grades);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.title_settings);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_settings);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.title_general_setting);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_general_setting_what);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_general_setting_anim);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_general_setting_dialog_stroke);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_general_setting_width_stroke);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_white_theme);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.title_themes);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.bottom_title_dark_theme);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = view.findViewById(R.id.Card_create);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.title_save_theme);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_name);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));



        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_icon);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_border);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_back);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_bar);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_bar);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_bar);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_light);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_dark);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_hint);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_cursor);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_card);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_bottom_border);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_add);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_add_plus);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_arrow);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_loading);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_conf);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_table);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_on);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_off);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_switch_on);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_switch_off);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_choose_back);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_choose_border);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_audio);
        textVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));


        View viewBorderVisual = view.findViewById(R.id.border_one);
        viewBorderVisual.setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
        viewBorderVisual = view.findViewById(R.id.border_two);
        viewBorderVisual.setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
        viewBorderVisual = view.findViewById(R.id.border_three);
        viewBorderVisual.setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
        viewBorderVisual = view.findViewById(R.id.border_four);
        viewBorderVisual.setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

        FrameLayout borderTheme = view.findViewById(R.id.border_white_theme);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_icon);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_border);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_back);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bar);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bar_text);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_bar);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_light);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_dark);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_hint);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_cursor);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_card);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bottom_border);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_add);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_add_plus);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));;
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_arrow);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_progress);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_conf);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_table);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_on);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_off);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_switch_on);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_switch_off);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_choose_back);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_choose_border);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_audio);
        borderTheme.setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));

        EditText editVisual = linearLayoutBottom.findViewById(R.id.custom_name);
        MainActivity.setCursorPointerColor(editVisual,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editVisual,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        editVisual.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editVisual.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

        final EditText editText = view.findViewById(R.id.EditDpDnew);
        MainActivity.setCursorPointerColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        editText.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        TextView textDpColor = view.findViewById(R.id.dpText);
        textDpColor.setTextColor(editText.getHintTextColors());
        editText.setHint(Integer.toString(settings.getInt("dpSizeSettings",120)));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (getActivity().getCurrentFocus() != null) {
                        View vw = getActivity().getCurrentFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                        editor.putInt("dpSizeSettings",Integer.parseInt(editText.getText().toString()));
                        editor.apply();
                        editText.clearFocus();
                        editText.setHint(editText.getText());
                        editText.setText(null);
                        List<Fragment> getDnewnikFragment = ((MainActivity) getActivity()).getDnewnikFragment();
                        DnewnikFragment dnewnikFragment = (DnewnikFragment) getDnewnikFragment.get(0);
                        dnewnikFragment.notifyTab();
                    }
                }
                return false;
            }
        });

        Switch switchNotify = view.findViewById(R.id.switchNotify);
        setSwitchColor(switchNotify, context);
            switchNotify.setChecked(settings.getBoolean("notifySettings",true));
            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        editor.putBoolean("notifySettings", b);
                         editor.apply();
                }
            });

        Switch switchWhat = view.findViewById(R.id.switchWhat);
        setSwitchColor(switchWhat, context);
            switchWhat.setChecked(settings.getBoolean("whatSettings",false));
            switchWhat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    editor.putBoolean("whatSettings", b);
                    editor.apply();
                }
            });

        Switch switchPoved = view.findViewById(R.id.switchPoved);
        setSwitchColor(switchPoved, context);
            switchPoved.setChecked(settings.getBoolean("PovedSettings",true));
            switchPoved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    editor.putBoolean("PovedSettings", b);
                    editor.apply();
                }
            });

        Switch switchAnimations = view.findViewById(R.id.switchAnim);
        setSwitchColor(switchAnimations, context);
        switchAnimations.setChecked(settings.getBoolean("AnimationSettings",true));
        switchAnimations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("AnimationSettings", b);
                editor.apply();
            }
        });

        final Switch switchWhiteTheme = view.findViewById(R.id.switchWhite);
        setSwitchColor(switchWhiteTheme, context);
        if(settings.getInt("id_current_theme", R.id.switchWhite) == R.id.switchWhite)
            switchWhiteTheme.setChecked(true);
        switchWhiteTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(settings.getInt("id_current_theme", R.id.switchWhite) == R.id.switchWhite)
                    switchWhiteTheme.setChecked(true);
                else
                    new ChangeTheme().execute(new String[]{Integer.toString(R.id.switchWhite), null,
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_icon)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_border_theme)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_background)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar_text)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_bar)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_text_light)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_text_dark)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_text_hint)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_cursor)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_card)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_bottomBorder)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add_plus)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_button_arrow)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_progress)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_not_confirmed)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_Table_column)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_on)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_off)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_on)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_off)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_background)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_border)),
                            Integer.toString(ContextCompat.getColor(context, R.color.custom_color_audio_player)),
                    });
            }
        });

        final Switch switchBlackTheme = view.findViewById(R.id.switchDark);
        setSwitchColor(switchBlackTheme, context);
        if(settings.getInt("id_current_theme", R.id.switchDark) == R.id.switchDark)
            switchBlackTheme.setChecked(true);
        switchBlackTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(settings.getInt("id_current_theme", R.id.switchDark) == R.id.switchDark)
                    switchBlackTheme.setChecked(true);
                else
                    new ChangeTheme().execute(new String[]{Integer.toString(R.id.switchDark), null,
                            Integer.toString(ContextCompat.getColor(context, R.color.black_icon)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_border_theme)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_background)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar_text)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_notification_bar)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_text_light)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_text_dark)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_text_hint)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_cursor)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_card)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_bottomBorder)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_button_add)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_button_add_plus)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_button_arrow)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_progress)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_not_confirmed)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_Table_column)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_notification_on)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_notification_off)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_switch_on)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_switch_off)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_background)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_border)),
                            Integer.toString(ContextCompat.getColor(context, R.color.black_color_audio_player)),
                    });
            }
        });

        Switch switchBorderAlert = view.findViewById(R.id.switchBorder_alert);
        setSwitchColor(switchBorderAlert, context);
        switchBorderAlert.setChecked(settings.getBoolean("BorderAlertSettings",false));
        switchBorderAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("BorderAlertSettings", b);
                editor.apply();
            }
        });

        Switch switchSaturday = view.findViewById(R.id.switchSaturday);
        setSwitchColor(switchSaturday, context);
        switchSaturday.setChecked(settings.getBoolean("SaturdaySettings",true));
        switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("SaturdaySettings", b);
                editor.apply();
            }
        });

        final EditText editTextSizeBorder = view.findViewById(R.id.EditSizeBorder);
        editTextSizeBorder.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editTextSizeBorder.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        MainActivity.setCursorPointerColor(editTextSizeBorder,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editTextSizeBorder,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        TextView textDpBorder = view.findViewById(R.id.dpBorder);
        textDpBorder.setTextColor(editTextSizeBorder.getHintTextColors());
        editTextSizeBorder.setHint(Integer.toString(settings.getInt("dpBorderSettings",4)));
        editTextSizeBorder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (getActivity().getCurrentFocus() != null) {
                        View vw = getActivity().getCurrentFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                        editor.putInt("dpBorderSettings",Integer.parseInt(editTextSizeBorder.getText().toString()));
                        editor.apply();
                        editTextSizeBorder.clearFocus();
                        editTextSizeBorder.setHint(editTextSizeBorder.getText());
                        editTextSizeBorder.setText(null);
                    }
                }
                return false;
            }
        });

        Spinner spinner = view.findViewById(R.id.spinner_default);
        spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
        List<String> choose = new ArrayList<>();
        if(settings.getString("dafauilt_choose_color",getString(R.string.not_chosen)).equals(getString(R.string.not_chosen))){
            choose.add(getString(R.string.not_chosen));
            choose.add(getString(R.string.HEX_code));
            choose.add(getString(R.string.Choose_color));
        }else if(settings.getString("dafauilt_choose_color",getString(R.string.not_chosen)).equals(getString(R.string.HEX_code))){
            choose.add(getString(R.string.HEX_code));
            choose.add(getString(R.string.not_chosen));
            choose.add(getString(R.string.Choose_color));
        }else{
            choose.add(getString(R.string.Choose_color));
            choose.add(getString(R.string.not_chosen));
            choose.add(getString(R.string.HEX_code));
        }
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context,choose,false);
        spinner.setAdapter(spinnerAdapter);
        spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        editor.putString("dafauilt_choose_color", spinnerAdapter.getItem(i));
                        editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Start();
        if(constrRecyclerViewArrayList.size() != 0) {
            recyclerView = view.findViewById(R.id.themes);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            adapter = new RecyclerThemeAdapter(constrRecyclerViewArrayList, context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.setOnCheckedChangeListener(new RecyclerThemeAdapter.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(int position, boolean b) {
                    if (constrRecyclerViewArrayList.get(position).getIdSwitch() == settings.getInt("id_current_theme", R.id.switchWhite)) {
                        adapter.getSwitch().setChecked(true);
                    }else
                    new ChangeTheme().execute(constrRecyclerViewArrayList.get(position).getColors());
                }
            });
            adapter.setOnItemLongClickListener(new RecyclerThemeAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(final int position) {
                    final LayoutInflater li = LayoutInflater.from(getActivity());
                    final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);

                    AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                    deleted.setView(promptsView);
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                    alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if(settings.getBoolean("BorderAlertSettings",false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                    promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);
                    final AlertDialog alertDialog = deleted.create();

                    TextView textTitle = promptsView.findViewById(R.id.title_alert);
                    textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                    textTitle.setText(context.getString(R.string.deleting));

                    TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                    textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    textBottomTitle.setText(context.getString(R.string.deleteTheme) + " \"" + constrRecyclerViewArrayList.get(position).getName() + "\" ?");

                    TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                    ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
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

                            StringBuffer stringBuffer = new StringBuffer();

                            try {
                                FileInputStream read = context.openFileInput("Themes.txt");
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String temp_read;
                                String[] help;
                                while ((temp_read = bufferedReader.readLine()) != null) {
                                    help = temp_read.split("=");
                                    if(!help[0].equals(Integer.toString(constrRecyclerViewArrayList.get(position).getIdSwitch())))
                                    stringBuffer.append(temp_read).append("\n");
                                }
                                bufferedReader.close();
                                reader.close();
                                read.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException ignore) {
                                ignore.printStackTrace();
                            }

                            try {
                                FileOutputStream write = getActivity().openFileOutput("Themes.txt", getActivity().MODE_PRIVATE);
                                String temp_write = stringBuffer.toString();

                                write.write(temp_write.getBytes());
                                write.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(constrRecyclerViewArrayList.get(position).getIdSwitch() == settings.getInt("id_current_theme", R.id.switchWhite))
                                new ChangeTheme().execute(new String[]{Integer.toString(R.id.switchWhite), null,
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_icon)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_border_theme)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar_text)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_bar)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_light)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_dark)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_hint)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_cursor)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_card)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_bottomBorder)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add_plus)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_arrow)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_progress)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_not_confirmed)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_Table_column)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_border)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_audio_player)),
                                });

                            constrRecyclerViewArrayList.remove(position);
                            if(settings.getBoolean("AnimationSettings",true)) {
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, constrRecyclerViewArrayList.size());
                            }else
                                adapter.notifyDataSetChanged();
                            alertDialog.hide();

                        }
                    });
                    ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                    ButtonSave.setText(getString(R.string.yes));

                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }
            });
            adapter.setOnItemClickListener(new RecyclerThemeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    ((MainActivity) getActivity()).EditTheme(constrRecyclerViewArrayList.get(position).getColors(), position);

                    new EditTheme().execute(constrRecyclerViewArrayList.get(position).getColors());
                }
            });
        }
        return view;
    }

    public void NotifyAdapter(int position, String newName){
        constrRecyclerViewArrayList.get(position).setName(newName);
        adapter.notifyDataSetChanged();
    }

    public static void setSwitchColor(Switch switchColor, Context context){
        SharedPreferences Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList buttonStates = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on)),
                            Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off))
                    }
            );
            switchColor.getThumbDrawable().setTintList(buttonStates);
            switchColor.getTrackDrawable().setTintList(buttonStates);
        }else{
            StateListDrawable thumbStates = new StateListDrawable();
            thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on))));
            thumbStates.addState(new int[]{}, new ColorDrawable(Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off)))); // this one has to come last
            switchColor.setThumbDrawable(thumbStates);
        }
    }

    class EditTheme extends AsyncTask<String[],String[],Void>{
        AlertDialog alertDialog;
        TextView textView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.loading_drawable);
            alertbackground.setColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.linerLoading).setBackground(alertbackground);
            ProgressBar progressBar = promptsView.findViewById(R.id.progress_loading_color);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            textView = promptsView.findViewById(R.id.textLoading);
            textView.setText(getString(R.string.loading));
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            alertDialog = progressDialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.hide();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

        @Override
        protected void onProgressUpdate(String[]... strings) {
            TextView name = linearLayoutBottom.findViewById(R.id.custom_name);
            name.setText(strings[0][1]);
            linearLayoutBottom.findViewById(R.id.custom_icon).setBackgroundColor(Integer.parseInt(strings[0][2]));
            linearLayoutBottom.findViewById(R.id.custom_border_theme).setBackgroundColor(Integer.parseInt(strings[0][3]));
            linearLayoutBottom.findViewById(R.id.custom_background).setBackgroundColor(Integer.parseInt(strings[0][4]));
            linearLayoutBottom.findViewById(R.id.custom_toolbar).setBackgroundColor(Integer.parseInt(strings[0][5]));
            linearLayoutBottom.findViewById(R.id.custom_toolbar_text).setBackgroundColor(Integer.parseInt(strings[0][6]));
            linearLayoutBottom.findViewById(R.id.custom_notification_bar).setBackgroundColor(Integer.parseInt(strings[0][7]));
            linearLayoutBottom.findViewById(R.id.custom_text_light).setBackgroundColor(Integer.parseInt(strings[0][8]));
            linearLayoutBottom.findViewById(R.id.custom_text_dark).setBackgroundColor(Integer.parseInt(strings[0][9]));
            linearLayoutBottom.findViewById(R.id.custom_text_hint).setBackgroundColor(Integer.parseInt(strings[0][10]));
            linearLayoutBottom.findViewById(R.id.custom_cursor).setBackgroundColor(Integer.parseInt(strings[0][11]));
            linearLayoutBottom.findViewById(R.id.custom_card).setBackgroundColor(Integer.parseInt(strings[0][12]));
            linearLayoutBottom.findViewById(R.id.custom_bottomBorder).setBackgroundColor(Integer.parseInt(strings[0][13]));
            linearLayoutBottom.findViewById(R.id.custom_button_add).setBackgroundColor(Integer.parseInt(strings[0][14]));
            linearLayoutBottom.findViewById(R.id.custom_button_add_plus).setBackgroundColor(Integer.parseInt(strings[0][15]));
            linearLayoutBottom.findViewById(R.id.custom_button_arrow).setBackgroundColor(Integer.parseInt(strings[0][16]));
            linearLayoutBottom.findViewById(R.id.custom_progress).setBackgroundColor(Integer.parseInt(strings[0][17]));
            linearLayoutBottom.findViewById(R.id.custom_not_confirmed).setBackgroundColor(Integer.parseInt(strings[0][18]));
            linearLayoutBottom.findViewById(R.id.custom_Table_column).setBackgroundColor(Integer.parseInt(strings[0][19]));
            linearLayoutBottom.findViewById(R.id.custom_notification_on).setBackgroundColor(Integer.parseInt(strings[0][20]));
            linearLayoutBottom.findViewById(R.id.custom_notification_off).setBackgroundColor(Integer.parseInt(strings[0][21]));
            linearLayoutBottom.findViewById(R.id.custom_switch_on).setBackgroundColor(Integer.parseInt(strings[0][22]));
            linearLayoutBottom.findViewById(R.id.custom_switch_off).setBackgroundColor(Integer.parseInt(strings[0][23]));
            linearLayoutBottom.findViewById(R.id.custom_color_block_choose_background).setBackgroundColor(Integer.parseInt(strings[0][24]));
            linearLayoutBottom.findViewById(R.id.custom_color_block_choose_border).setBackgroundColor(Integer.parseInt(strings[0][25]));
            linearLayoutBottom.findViewById(R.id.custom_color_audio_player).setBackgroundColor(Integer.parseInt(strings[0][26]));
        }

        @Override
        protected Void doInBackground(String[]... strings) {
           publishProgress(strings[0]);
            return null;
        }
    }

    class ChangeTheme extends AsyncTask<String[],Void,Void>{
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            TextView textView = promptsView.findViewById(R.id.textLoading);
            textView.setText(getString(R.string.Apply_Theme));
            alertDialog = progressDialog.create();
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.hide();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        @Override
        protected Void doInBackground(String[]... strings) {
            editor.putInt("id_current_theme", Integer.parseInt(strings[0][0]));
            editor.apply();

            SharedPreferences.Editor editorColor = Current_Theme.edit();

            editorColor.putInt("custom_icon", Integer.parseInt(strings[0][2]));
            editorColor.putInt("custom_border_theme", Integer.parseInt(strings[0][3]));
            editorColor.putInt("custom_background", Integer.parseInt(strings[0][4]));
            editorColor.putInt("custom_toolbar", Integer.parseInt(strings[0][5]));
            editorColor.putInt("custom_toolbar_text", Integer.parseInt(strings[0][6]));
            editorColor.putInt("custom_notification_bar", Integer.parseInt(strings[0][7]));
            editorColor.putInt("custom_text_light", Integer.parseInt(strings[0][8]));
            editorColor.putInt("custom_text_dark", Integer.parseInt(strings[0][9]));
            editorColor.putInt("custom_text_hint", Integer.parseInt(strings[0][10]));
            editorColor.putInt("custom_cursor", Integer.parseInt(strings[0][11]));
            editorColor.putInt("custom_card", Integer.parseInt(strings[0][12]));
            editorColor.putInt("custom_bottomBorder", Integer.parseInt(strings[0][13]));
            editorColor.putInt("custom_button_add", Integer.parseInt(strings[0][14]));
            editorColor.putInt("custom_button_add_plus", Integer.parseInt(strings[0][15]));
            editorColor.putInt("custom_button_arrow", Integer.parseInt(strings[0][16]));
            editorColor.putInt("custom_progress", Integer.parseInt(strings[0][17]));
            editorColor.putInt("custom_not_confirmed", Integer.parseInt(strings[0][18]));
            editorColor.putInt("custom_Table_column", Integer.parseInt(strings[0][19]));
            editorColor.putInt("custom_notification_on", Integer.parseInt(strings[0][20]));
            editorColor.putInt("custom_notification_off", Integer.parseInt(strings[0][21]));
            editorColor.putInt("custom_switch_on", Integer.parseInt(strings[0][22]));
            editorColor.putInt("custom_switch_off", Integer.parseInt(strings[0][23]));
            editorColor.putInt("custom_color_block_choose_background", Integer.parseInt(strings[0][24]));
            editorColor.putInt("custom_color_block_choose_border", Integer.parseInt(strings[0][25]));
            editorColor.putInt("custom_color_audio_player", Integer.parseInt(strings[0][26]));

            editorColor.apply();

            return null;
        }
    }

    private void Start() {
        String[] help ;
        String delimeter = "=";
        constrRecyclerViewArrayList.clear();

        try {
            FileInputStream read = getActivity().openFileInput("Themes.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
                help = temp_read.split(delimeter);
                constrRecyclerViewArrayList.add(new ConstrThemeRecycler(help));
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}