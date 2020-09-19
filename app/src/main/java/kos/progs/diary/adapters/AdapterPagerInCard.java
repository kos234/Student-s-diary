package kos.progs.diary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import kos.progs.diary.constructors.ConstructorDnewnik;
import kos.progs.diary.constructors.ConstructorItemCard;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterPagerInCard extends PagerAdapter {
    public final ArrayList<ConstructorDnewnik> ConstructorDnewniks;
    private final Context context;
    private final SharedPreferences Current_Theme;
    private final ArrayList<RecyclerView> scrollViewArrayList = new ArrayList<>();
    private final int[] scrolls;
    private final String[] idTextView;


    public AdapterPagerInCard(ArrayList<ConstructorDnewnik> ConstructorDnewniks, Context context, int[] scrolls, String[] idTextView) {
        this.ConstructorDnewniks = ConstructorDnewniks;
        this.context = context;
        this.scrolls = scrolls;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        this.idTextView = idTextView;
    }

    @Override
    public int getCount() {
        try {
            return ConstructorDnewniks.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        try {
            return view.equals(object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_view, container, false);
        try {
            TextView NameDay;
            ConstructorDnewnik array = ConstructorDnewniks.get(position);
            final RecyclerView recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            if (scrolls.length > position)
                recyclerView.post(() -> {
                    try {
                        recyclerView.setScrollY(scrolls[position]);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
            scrollViewArrayList.add(recyclerView);
            CardView cardView = view.findViewById(R.id.card_table);
            cardView.setCardBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));

            List<String> arrayList = array.strings;
            ArrayList<ConstructorItemCard> constructorItemCards = new ArrayList<>();
            String[] explodeString, temp4;
            StringBuilder tempik = new StringBuilder();

            for (int q = 0; q < arrayList.size(); q++) {
                explodeString = arrayList.get(q).split("=");

                temp4 = explodeString[2].split("/\\*/");
                if (temp4.length == 1)
                    constructorItemCards.add(new ConstructorItemCard(explodeString[0], explodeString[1], temp4[0], explodeString[3], array.isUmply, position, idTextView));
                else {
                    for (int n = 0; n < temp4.length; n++) {
                        if (n + 1 == temp4.length)
                            tempik.append(temp4[n]);
                        else
                            tempik.append(temp4[n]).append("\n");
                    }
                    constructorItemCards.add(new ConstructorItemCard(explodeString[0], explodeString[1], tempik.toString(), explodeString[3], array.isUmply, position, idTextView));
                }

            }
            NameDay = view.findViewById(R.id.textViewNameDay);
            NameDay.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            NameDay.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            NameDay.setText(array.nameDay);
            TextView tapLesion = view.findViewById(R.id.card_tab_one);
            tapLesion.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            tapLesion.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            TextView tapHomeWork = view.findViewById(R.id.card_tab_two);
            tapHomeWork.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            tapHomeWork.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            TextView tapGrades = view.findViewById(R.id.card_tab_three);
            tapGrades.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            tapGrades.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            AdapterCardItem adapter = new AdapterCardItem(constructorItemCards, tapLesion, tapHomeWork, tapGrades, context);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            container.addView(view, 0);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public int[] getScrolls() {
        try {
            int[] scrollInt = new int[scrollViewArrayList.size()];
            for (int i = 0; i < scrollViewArrayList.size(); i++) {
                scrollInt[i] = scrollViewArrayList.get(i).getScrollY();
            }

            return scrollInt;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return new int[]{0};
        }
    }


}