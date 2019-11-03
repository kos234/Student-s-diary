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

import java.util.List;

public class PagerAdapterInCard extends PagerAdapter {
    private List<helperDnewnik> helperDnewniks;
    private Context context;


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
        TextView Predmet,Kab,Dz,NameDay;
        String help = null;
        String help1= null;
        String help2= null;

        help = helperDnewniks.get(position).getNamePred();
        help1 =  helperDnewniks.get(position).getKab();
        help2 = helperDnewniks.get(position).getDz();
        String[] temp = help.split("=");
        String[] temp1 = help1.split("=");
        String[] temp2 = help2.split("=");
        for (int q = 0; q < help.split("=").length; q++){
            TableLayout tableLayout = view.findViewById(R.id.tableDnew);
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            TableRow tr = (TableRow) inflater.inflate(R.layout.item_table, null);
            Predmet = tr.findViewById(R.id.textView1_1_dnev);
            Kab = tr.findViewById(R.id.textView1_2_dnev);
            Dz = tr.findViewById(R.id.textView1_3_dnev);
            Predmet.setText(temp[q]);
            Kab.setText(temp1[q]);
            String[] temp3 = temp2[q].split("`");
            String tempik = " ";
            if (temp3.length == 1)
            Dz.setText(temp3[0]);
            else {
                for (int n = 0; n < temp3.length; n++) {
                    if(n+1 == temp3.length)
                        tempik = tempik + temp3[n];
                        else
                    tempik = tempik + temp3[n] + "\n";
                }
                Dz.setText(tempik);
            }
            tableLayout.addView(tr);
        }
        NameDay = view.findViewById(R.id.textViewNameDay);
        NameDay.setText(helperDnewniks.get(position).getNameDay());
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
