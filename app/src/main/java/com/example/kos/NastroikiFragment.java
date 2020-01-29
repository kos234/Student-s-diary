package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class NastroikiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings,
            Current_Theme;
    private SharedPreferences.Editor editor;
    private ArrayList<ConstrThemeRecycler> constrRecyclerViewArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerThemeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nastroiki, container, false);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        editor = settings.edit();

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((LinearLayout) view.findViewById(R.id.field_create_fragment));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        final EditText editText = view.findViewById(R.id.EditDpDnew);
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

        final Switch switchWhiteTheme = view.findViewById(R.id.switchWhite);
        if(settings.getInt("id_current_theme", R.id.switchWhite) == R.id.switchWhite)
            switchWhiteTheme.setChecked(true);
        switchWhiteTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(settings.getInt("id_current_theme", R.id.switchWhite) == R.id.switchWhite)
                    switchWhiteTheme.setChecked(true);
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
        }
        return view;
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
            editorColor.putInt("custom_card", Integer.parseInt(strings[0][11]));
            editorColor.putInt("custom_bottomBorder", Integer.parseInt(strings[0][12]));
            editorColor.putInt("custom_button_add", Integer.parseInt(strings[0][13]));
            editorColor.putInt("custom_button_add_plus", Integer.parseInt(strings[0][14]));
            editorColor.putInt("custom_button_arrow", Integer.parseInt(strings[0][15]));
            editorColor.putInt("custom_progress", Integer.parseInt(strings[0][16]));
            editorColor.putInt("custom_not_confirmed", Integer.parseInt(strings[0][17]));
            editorColor.putInt("custom_Table_column", Integer.parseInt(strings[0][18]));
            editorColor.putInt("custom_notification_on", Integer.parseInt(strings[0][19]));
            editorColor.putInt("custom_notification_off", Integer.parseInt(strings[0][20]));
            editorColor.putInt("custom_switch_on", Integer.parseInt(strings[0][21]));
            editorColor.putInt("custom_switch_on_background", Integer.parseInt(strings[0][22]));

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
