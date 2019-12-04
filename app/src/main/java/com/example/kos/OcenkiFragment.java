package com.example.kos;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OcenkiFragment extends Fragment {
    private Context context;

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

        /**
         * Сделать поле подтверждение в котором можно либо сделать фотографию оценок или росписи учителя либо записать голосовое сообщение учителя
         * Блоки или таблицы пока не знаю но скорее всего блоки
         * Если поле не подтверждено то оно подсвечивается крассным
         */
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
