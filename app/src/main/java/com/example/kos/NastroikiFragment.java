package com.example.kos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class NastroikiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nastroiki, container, false);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        editor = settings.edit();

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
/**
 * Стили
 * Анимации
 * Уведомления
 *
 */
        final EditText editText = view.findViewById(R.id.EditDpDnew);
        TextView textDpColor = view.findViewById(R.id.dpText);
        textDpColor.setTextColor(editText.getHintTextColors());
        editText.setHint(Integer.toString(settings.getInt("dpSizeSettings",120)));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    //Hide keyboard
                    if (getActivity().getCurrentFocus() != null) {
                        View vw = getActivity().getCurrentFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                        editor.putInt("dpSizeSettings",Integer.parseInt(editText.getText().toString()));
                        editor.apply();
                        editText.clearFocus();
                        editText.setHint(editText.getText());
                        editText.setText(null);
                    }
                }
                return false;
            }
        });

        Switch switchNotify = view.findViewById(R.id.switchNotify);
            switchNotify.setChecked(settings.getBoolean("notifySettings",true));
            switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        editor.putBoolean("notifySettings", b);
                         editor.apply();
                }
            });

        Switch switchWhat = view.findViewById(R.id.switchWhat);
            switchWhat.setChecked(settings.getBoolean("whatSettings",false));
            switchWhat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    editor.putBoolean("whatSettings", b);
                    editor.apply();
                }
            });

        Switch switchPoved = view.findViewById(R.id.switchPoved);
            switchPoved.setChecked(settings.getBoolean("PovedSettings",true));
            switchPoved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    editor.putBoolean("PovedSettings", b);
                    editor.apply();
                }
            });

        Switch switchAnimations = view.findViewById(R.id.switchAnim);
        switchAnimations.setChecked(settings.getBoolean("AnimationSettings",true));
        switchAnimations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("AnimationSettings", b);
                editor.apply();
            }
        });

        return view;
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
