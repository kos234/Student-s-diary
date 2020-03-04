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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class NastroikiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings,
            Current_Theme;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBottom;
    private SharedPreferences.Editor editor;
    private final ArrayList<ConstrThemeRecycler> constrRecyclerViewArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerThemeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_nastroiki, container, false);
        try{
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        editor = settings.edit();

        Toolbar toolbar = view.findViewById(R.id.toolbar4);
        Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
        menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(menuToolbar);        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                ((MainActivity) Objects.requireNonNull(getActivity())).openDrawer();
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            }
        });
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));

        linearLayoutBottom = view.findViewById(R.id.field_create_fragment);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //Visual

        final int cardColor = Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)), textLightcolor = Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), borderColor = Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)), borderThemeColor = Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme));
        linearLayoutBottom.findViewById(R.id.field_create_fragment).setBackgroundColor(cardColor);
        CardView cardVisual = view.findViewById(R.id.card_students);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_timetables);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_grades);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_settings);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_general_setting);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_themes);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_white_theme);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_dark_theme);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = view.findViewById(R.id.card_create_theme_nastroiki);
        cardVisual.setCardBackgroundColor(cardColor);
        cardVisual = linearLayoutBottom.findViewById(R.id.save_theme);
        cardVisual.setCardBackgroundColor(cardColor);

        TextView textVisual = view.findViewById(R.id.title_students);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_students);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.title_timetables);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_timetables_not);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_timetables_saturday);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.title_grades);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_grades);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.title_settings);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_settings);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.title_general_setting);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_general_setting_what);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_general_setting_anim);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_general_setting_dialog_stroke);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_general_setting_width_stroke);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_white_theme);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.title_themes);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_dark_theme);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.Card_create);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.title_save_theme);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_name);
        textVisual.setTextColor(textLightcolor);
        textVisual = view.findViewById(R.id.bottom_title_general_setting_fragment_default);
        textVisual.setTextColor(textLightcolor);



        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_icon);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_border);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_back);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_bar);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_bar);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_bar);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_light);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_dark);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_text_hint);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_cursor);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_card);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_bottom_border);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_add);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_add_plus);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_arrow);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_loading);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_conf);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_table);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_on);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_not_off);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_switch_on);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_switch_off);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_choose_back);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_choose_border);
        textVisual.setTextColor(textLightcolor);
        textVisual = linearLayoutBottom.findViewById(R.id.bottom_title_theme_audio);
        textVisual.setTextColor(textLightcolor);


        View viewBorderVisual = view.findViewById(R.id.border_one);
        viewBorderVisual.setBackgroundColor(borderColor);
        viewBorderVisual = view.findViewById(R.id.border_two);
        viewBorderVisual.setBackgroundColor(borderColor);
        viewBorderVisual = view.findViewById(R.id.border_three);
        viewBorderVisual.setBackgroundColor(borderColor);
        viewBorderVisual = view.findViewById(R.id.border_four);
        viewBorderVisual.setBackgroundColor(borderColor);
        viewBorderVisual = view.findViewById(R.id.border_five);
        viewBorderVisual.setBackgroundColor(borderColor);

        FrameLayout borderTheme = view.findViewById(R.id.border_white_theme);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_icon);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_border);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_back);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bar);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bar_text);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_bar);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_light);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_dark);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_text_hint);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_cursor);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_card);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_bottom_border);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_add);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_add_plus);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_arrow);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_progress);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_conf);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_table);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_on);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_not_off);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_switch_on);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_switch_off);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_choose_back);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_choose_border);
        borderTheme.setBackgroundColor(borderThemeColor);
        borderTheme = linearLayoutBottom.findViewById(R.id.border_theme_audio);
        borderTheme.setBackgroundColor(borderThemeColor);

        EditText editVisual = linearLayoutBottom.findViewById(R.id.custom_name);
        MainActivity.setCursorPointerColor(editVisual,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editVisual,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        editVisual.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editVisual.setTextColor(textLightcolor);

        final EditText editText = view.findViewById(R.id.EditDpDnew);
        MainActivity.setCursorPointerColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        editText.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editText.setTextColor(textLightcolor);
        TextView textDpColor = view.findViewById(R.id.dpText);
        textDpColor.setTextColor(editText.getHintTextColors());
        editText.setHint(Integer.toString(settings.getInt("dpSizeSettings",120)));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try{
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (Objects.requireNonNull(getActivity()).getCurrentFocus() != null) {
                        View vw = getActivity().getCurrentFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(vw.getWindowToken(), 0);
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
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
                return false;
            }
        });

        Switch switchNotify = view.findViewById(R.id.switchNotify);
        setSwitchColor(switchNotify, context);
            switchNotify.setChecked(settings.getBoolean("notifySettings",true));
            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try{
                        editor.putBoolean("notifySettings", b);
                         editor.apply();
                    }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
                }
            });

        Switch switchWhat = view.findViewById(R.id.switchWhat);
        setSwitchColor(switchWhat, context);
            switchWhat.setChecked(settings.getBoolean("whatSettings",false));
            switchWhat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try{
                    editor.putBoolean("whatSettings", b);
                    editor.apply();
                    }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
                }
            });

        Switch switchPoved = view.findViewById(R.id.switchPoved);
        setSwitchColor(switchPoved, context);
            switchPoved.setChecked(settings.getBoolean("PovedSettings",true));
            switchPoved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try{
                    editor.putBoolean("PovedSettings", b);
                    editor.apply();
                    }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
                }
            });

        Switch switchAnimations = view.findViewById(R.id.switchAnim);
        setSwitchColor(switchAnimations, context);
        switchAnimations.setChecked(settings.getBoolean("AnimationSettings",true));
        switchAnimations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
                editor.putBoolean("AnimationSettings", b);
                editor.apply();
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            }
        });

        final Switch switchWhiteTheme = view.findViewById(R.id.switchWhite);
        setSwitchColor(switchWhiteTheme, context);
        if(settings.getInt("id_current_theme", R.id.switchWhite) == R.id.switchWhite)
            switchWhiteTheme.setChecked(true);
        switchWhiteTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
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
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);} }

        });

        final Switch switchBlackTheme = view.findViewById(R.id.switchDark);
        setSwitchColor(switchBlackTheme, context);
        if(settings.getInt("id_current_theme", R.id.switchDark) == R.id.switchDark)
            switchBlackTheme.setChecked(true);
        switchBlackTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
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
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}
        });

        Switch switchBorderAlert = view.findViewById(R.id.switchBorder_alert);
        setSwitchColor(switchBorderAlert, context);
        switchBorderAlert.setChecked(settings.getBoolean("BorderAlertSettings",false));
        switchBorderAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
                editor.putBoolean("BorderAlertSettings", b);
                editor.apply();
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            }
        });

        Switch switchSaturday = view.findViewById(R.id.switchSaturday);
        setSwitchColor(switchSaturday, context);
        switchSaturday.setChecked(settings.getBoolean("SaturdaySettings",true));
        switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try{
                editor.putBoolean("SaturdaySettings", b);
                editor.apply();

                    List<Fragment> getDnewnikFragment = ((MainActivity) getActivity()).getDnewnikFragment();
                    DnewnikFragment dnewnikFragment = (DnewnikFragment) getDnewnikFragment.get(0);
                    dnewnikFragment.notifySaturday();

                    ZnonkiFragment znonkiFragment = (ZnonkiFragment) getDnewnikFragment.get(1);
                    znonkiFragment.notifySaturday();

                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            }
        });

        final EditText editTextSizeBorder = view.findViewById(R.id.EditSizeBorder);
        editTextSizeBorder.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
        editTextSizeBorder.setTextColor(textLightcolor);
        MainActivity.setCursorPointerColor(editTextSizeBorder,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        MainActivity.setCursorColor(editTextSizeBorder,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        TextView textDpBorder = view.findViewById(R.id.dpBorder);
        textDpBorder.setTextColor(editTextSizeBorder.getHintTextColors());
        editTextSizeBorder.setHint(Integer.toString(settings.getInt("dpBorderSettings",4)));
        editTextSizeBorder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try{
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (Objects.requireNonNull(getActivity()).getCurrentFocus() != null) {
                        View vw = getActivity().getCurrentFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(vw.getWindowToken(), 0);
                        editor.putInt("dpBorderSettings",Integer.parseInt(editTextSizeBorder.getText().toString()));
                        editor.apply();
                        editTextSizeBorder.clearFocus();
                        editTextSizeBorder.setHint(editTextSizeBorder.getText());
                        editTextSizeBorder.setText(null);
                    }
                }
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
                return false;
            }
        });

        Spinner spinner = view.findViewById(R.id.spinner_default);
        spinner.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
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
        spinner.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                        editor.putString("dafauilt_choose_color", spinnerAdapter.getItem(i));
                        editor.apply();
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Spinner spinnerFragment = view.findViewById(R.id.spinner_fragment_default);
        spinnerFragment.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
        List<String> chooseFragment = new ArrayList<>();
        String switchValue = settings.getString("dafauilt_fragment",getString(R.string.fragment_default_off));
        if(switchValue.equals(getString(R.string.app_name))) {
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else if(switchValue.equals(getString(R.string.timetables))) {
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else if(switchValue.equals(getString(R.string.teachers))) {
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else if(switchValue.equals(getString(R.string.grades))) {
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else if(switchValue.equals(getString(R.string.settings))) {
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else if(switchValue.equals(getString(R.string.help))) {
            chooseFragment.add(getString(R.string.help));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.fragment_default_off));
        }else {
            chooseFragment.add(getString(R.string.fragment_default_off));
            chooseFragment.add(getString(R.string.app_name));
            chooseFragment.add(getString(R.string.timetables));
            chooseFragment.add(getString(R.string.teachers));
            chooseFragment.add(getString(R.string.grades));
            chooseFragment.add(getString(R.string.settings));
            chooseFragment.add(getString(R.string.help));
        }

        final SpinnerAdapter spinnerAdapterFragment = new SpinnerAdapter(context,chooseFragment,false);
        spinnerFragment.setAdapter(spinnerAdapterFragment);
        spinnerFragment.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
        spinnerFragment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { try{
                editor.putString("dafauilt_fragment", spinnerAdapterFragment.getItem(i));
                editor.apply();
            }catch (Exception error){((MainActivity) getActivity()).errorStack(error);} }

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
                public void onCheckedChanged(int position) {try{
                    if (constrRecyclerViewArrayList.get(position).getIdSwitch() == settings.getInt("id_current_theme", R.id.switchWhite)) {
                        adapter.getSwitch().setChecked(true);
                    }else
                    new ChangeTheme().execute(constrRecyclerViewArrayList.get(position).getColors());
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}
            });
            adapter.setOnItemLongClickListener(new RecyclerThemeAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(final int position) {try{
                    final LayoutInflater li = LayoutInflater.from(getActivity());
                    final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);

                    AlertDialog.Builder deleted = new AlertDialog.Builder(getActivity());
                    deleted.setView(promptsView);
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                    Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if(settings.getBoolean("BorderAlertSettings",false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                    promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);
                    final AlertDialog alertDialog = deleted.create();

                    TextView textTitle = promptsView.findViewById(R.id.title_alert);
                    textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                    textTitle.setText(context.getString(R.string.deleting));

                    TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                    textBottomTitle.setTextColor(textLightcolor);
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

                            StringBuilder stringBuffer = new StringBuilder();

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
                                FileOutputStream write = Objects.requireNonNull(getActivity()).openFileOutput("Themes.txt", getActivity().MODE_PRIVATE);
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

                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}
            });
            adapter.setOnItemClickListener(new RecyclerThemeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {try{
                    ((MainActivity) Objects.requireNonNull(getActivity())).EditTheme(constrRecyclerViewArrayList.get(position).getColors(), position);

                    new EditTheme().execute(constrRecyclerViewArrayList.get(position).getColors());
                }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}
            });
        }
        }catch (Exception error){((MainActivity) context).errorStack(error);}

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
        protected void onPreExecute() {try{
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.loading_drawable);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.linerLoading).setBackground(alertbackground);
            ProgressBar progressBar = promptsView.findViewById(R.id.progress_loading_color);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            textView = promptsView.findViewById(R.id.textLoading);
            textView.setText(getString(R.string.loading));
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            alertDialog = progressDialog.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            alertDialog.hide();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}

        @Override
        protected void onProgressUpdate(String[]... strings) {try{
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
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}

        @Override
        protected Void doInBackground(String[]... strings) {try{
           publishProgress(strings[0]);
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
        return null;
        }
    }

    class ChangeTheme extends AsyncTask<String[],Void,Void>{
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {try{
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.loading_drawable);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.linerLoading).setBackground(alertbackground);
            ProgressBar progressBar = promptsView.findViewById(R.id.progress_loading_color);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            TextView textView = promptsView.findViewById(R.id.textLoading);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textView.setText(getString(R.string.Apply_Theme));
            alertDialog = progressDialog.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}

        @Override
        protected void onPostExecute(Void aVoid) {try{
            super.onPostExecute(aVoid);
            alertDialog.hide();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}}


        @Override
        protected Void doInBackground(String[]... strings) {try{
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
        }catch (Exception error){((MainActivity) getActivity()).errorStack(error);}
            return null;
        }
    }

    private void Start() {
        String[] help ;
        String delimeter = "=";
        constrRecyclerViewArrayList.clear();

        try {
            FileInputStream read = Objects.requireNonNull(getActivity()).openFileInput("Themes.txt");
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