package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class OcenkiFragment extends Fragment {
    private Context context;
    private SharedPreferences settings = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
    final SharedPreferences.Editor editor = settings.edit();
    private TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocenki, container, false);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar5);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        tableLayout = view.findViewById(R.id.TableGrades);
        //!settings.contains("NumQuarters") ||
        if(settings.getBoolean("BoolNumQuarters",true)){
                final LayoutInflater li = LayoutInflater.from(getActivity());
                final View promptsView = li.inflate(R.layout.add_ychitelia , null);
                AlertDialog.Builder newadd = new AlertDialog.Builder(getActivity());
                newadd.setView(promptsView);
                final EditText NumQuarters = (EditText) promptsView.findViewById(R.id.NumQuartalers);
                newadd.setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NumQuarters.getText().toString().equals(""))
                            editor.putInt("NumQuarters",4);
                        else
                            editor.putInt("NumQuarters",Integer.parseInt(NumQuarters.getText().toString()));
                        editor.putBoolean("BoolNumQuarters",false);
                        new StartAsyncTask().execute();
                    }
                });
                AlertDialog alertDialog = newadd.create();

                //и отображаем его:

                alertDialog.show();
            }
            else {
                new StartAsyncTask().execute();
            }
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

    public class StartAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(context);
            tableLayout.addView(progressBar);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }
    }


}
