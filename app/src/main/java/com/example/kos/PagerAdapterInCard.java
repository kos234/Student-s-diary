package com.example.kos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapterInCard extends PagerAdapter {
    private List<helperDnewnik> helperDnewniks;
    private Context context;
    private TableLayout tableLayout;
    private TableRow tableRow1,tableRow2,tableRow3;

    public PagerAdapterInCard(List<helperDnewnik> helperDnewniks, Context context) {
        this.helperDnewniks = helperDnewniks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return helperDnewniks.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_card_view,container,false);
        TextView Predmet,Kab,Dz;
        ArrayList<String> help;
        ArrayList<String> help1;
        ArrayList<String> help2;
        Kab = view.findViewById(R.id.textView1_2_dnev);
        Dz = view.findViewById(R.id.textView1_3_dnev);

        help = helperDnewniks.get(position).getNamePred();

        help1 =  helperDnewniks.get(position).getKab();
        help2 = helperDnewniks.get(position).getDz();
        for (int i = 0 ; i < help.size(); i++){
            TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableDnew);
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            TableRow tr = (TableRow) inflater.inflate(R.layout.item_table, null);
            Predmet = tr.findViewById(R.id.textView1_1_dnev);
            Kab = tr.findViewById(R.id.textView1_2_dnev);
            Dz = tr.findViewById(R.id.textView1_3_dnev);
            Predmet.setText(help.get(i));
            Kab.setText(help1.get(i));
            Dz.setText(help2.get(i));
            tableLayout.addView(tr);
        }
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
