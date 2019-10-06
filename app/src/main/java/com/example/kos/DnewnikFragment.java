package com.example.kos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DnewnikFragment extends Fragment {
    private Context context;

    private List<helperDnewnik> helperDnewniks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dnewnik, container, false);
        Starting(view);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
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


    public void Starting(View view) {

        String[] help, helpKab;
        ArrayList<String> NamePred = new ArrayList<>();
        ArrayList<String> Kab = new ArrayList<>();
        ArrayList<String> Dz = new ArrayList<>();
        try {
            FileInputStream read =  getActivity().openFileInput("Monday.txt");
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp_read;
            while ((temp_read = bufferedReader.readLine()) != null) {
                help = temp_read.split("=");

                helpKab = help[1].split(",");
                NamePred.add(helpKab[0]);
                Kab.add(helpKab[1].substring(1));
                Dz.add(help[2]);

            }
            helperDnewniks.add(new helperDnewnik(NamePred,Kab,Dz));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
        ViewPager viewPager = view.findViewById(R.id.viewPagerDnewnik);
        viewPager.setAdapter(pagerAdapterInCard);
        viewPager.setPadding(50, 0, 50, 0);

    }
}
