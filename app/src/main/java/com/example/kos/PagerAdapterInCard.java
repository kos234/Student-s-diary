package com.example.kos;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PagerAdapterInCard extends PagerAdapter {
    private final List<helperDnewnik> helperDnewniks;
    private final Context context;
    private final SharedPreferences Current_Theme;


    public PagerAdapterInCard(List<helperDnewnik> helperDnewniks, Context context) {
        this.helperDnewniks = helperDnewniks;
        this.context = context;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
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
        TextView Predmet,Kab,Dz,NameDay, Ocenka;
        String help;
        CardView cardView = view.findViewById(R.id.card_table);
        cardView.setCardBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));
        help = helperDnewniks.get(position).getNamePred();
        String[] temp = help.split("=");
        String[] temp1 = helperDnewniks.get(position).getKab().split("=");
        String[] temp2 = helperDnewniks.get(position).getDz().split("=");
        String[] temp3 = helperDnewniks.get(position).getOcenka().split("=");
        for (int q = 0; q < help.split("=").length; q++){
            TableLayout tableLayout = view.findViewById(R.id.tableDnew);
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            TableRow tr = (TableRow) inflater.inflate(R.layout.item_table, null);
            Predmet = tr.findViewById(R.id.textView1_1_dnev);
            Predmet.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            Predmet.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            Kab = tr.findViewById(R.id.textView1_2_dnev);
            Kab.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            Kab.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            Dz = tr.findViewById(R.id.textView1_3_dnev);
            Dz.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            Dz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            Ocenka = tr.findViewById(R.id.textView1_4_dnev);
            Ocenka.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            Ocenka.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            Predmet.setText(temp[q]);
            Kab.setText(temp1[q]);
            Ocenka.setText(temp3[q]);
            String[] temp4 = temp2[q].split("`");
            StringBuilder tempik = new StringBuilder(" ");
            if (temp4.length == 1)
            Dz.setText(temp4[0]);
            else {
                for (int n = 0; n < temp4.length; n++) {
                    if(n+1 == temp4.length)
                        tempik.append(temp4[n]);
                        else
                    tempik.append(temp4[n]).append("\n");
                }
                Dz.setText(tempik.toString());
            }
            tableLayout.addView(tr);
        }
        NameDay = view.findViewById(R.id.textViewNameDay);
        view.findViewById(R.id.NameDay).setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        NameDay.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        NameDay.setText(helperDnewniks.get(position).getNameDay());
        TextView tempTab =  view.findViewById(R.id.card_tab_one);
        tempTab.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        tempTab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

        tempTab =  view.findViewById(R.id.card_tab_two);
        tempTab.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        tempTab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        tempTab =  view.findViewById(R.id.card_tab_three);
        tempTab.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        tempTab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}