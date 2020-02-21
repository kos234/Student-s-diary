package com.example.kos;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class SpravkaFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spravka, container, false);
        SharedPreferences Current_Theme = Objects.requireNonNull(getContext()).getSharedPreferences("Current_Theme", MODE_PRIVATE);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar5);
        Drawable menuToolbar = getResources().getDrawable(R.drawable.ic_menu_24px);
        menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(getContext(), R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(menuToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { ((MainActivity) Objects.requireNonNull(getActivity())).openDrawer(); }});
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(getContext(), R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(getContext(), R.color.custom_toolbar)));

        TextView textViewObz = view.findViewById(R.id.obz_one);
        textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(getContext(), R.color.custom_text_light)));
        textViewObz = view.findViewById(R.id.obz_two);
        textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(getContext(), R.color.custom_text_light)));
        textViewObz = view.findViewById(R.id.obz_three);
        textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(getContext(), R.color.custom_text_light)));
        textViewObz = view.findViewById(R.id.obz_four);
        textViewObz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(getContext(), R.color.custom_text_light)));
        return view;
    }

}