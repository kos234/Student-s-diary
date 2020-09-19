package kos.progs.diary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import kos.progs.diary.constructors.ConstructorItemMenu;
import kos.progs.diary.fragments.FragmentBells;
import kos.progs.diary.fragments.FragmentDnewnik;
import kos.progs.diary.fragments.FragmentGrades;
import kos.progs.diary.fragments.FragmentHelp;
import kos.progs.diary.fragments.FragmentSettings;
import kos.progs.diary.fragments.FragmentTeachers;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;

import java.util.ArrayList;

public class AdapterMenuRecyclerView extends RecyclerView.Adapter<AdapterMenuRecyclerView.ViewHolder> {

    final Context context;
    final ArrayList<ConstructorItemMenu> arrayList;
    final ArrayList<View> views = new ArrayList<>();
    final SharedPreferences Current_Theme;
    final FragmentManager fragmentManager;


    public AdapterMenuRecyclerView(Context context, ArrayList<ConstructorItemMenu> arrayList, FragmentManager fragmentManager) {
        this.context = context;
        this.arrayList = arrayList;
        Current_Theme = context.getSharedPreferences("Current_Theme", Context.MODE_PRIVATE);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            ConstructorItemMenu constructorItemMenu = arrayList.get(position);
            Drawable drawable = MainActivity.getResources.getDrawable(constructorItemMenu.icon);
            drawable.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            holder.icon.setImageDrawable(drawable);
            holder.tittle.setText(constructorItemMenu.title);
            holder.tittle.setTextColor(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)));
            if (constructorItemMenu.icChecked)
                holder.rootView.setBackgroundColor(Current_Theme.getInt("custom_selected_section", ContextCompat.getColor(context, R.color.custom_selected_section)));
            else
                holder.rootView.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            views.add(holder.rootView);
            holder.rootView.setOnClickListener(v -> {
                try {
                    Class<?> fragmentClass = null;
                    switch (position) {
                        case 1:
                            fragmentClass = FragmentBells.class;
                            break;
                        case 2:
                            fragmentClass = FragmentTeachers.class;
                            break;
                        case 3:
                            fragmentClass = FragmentGrades.class;
                            break;
                        case 4:
                            fragmentClass = FragmentSettings.class;
                            break;
                        case 5:
                            fragmentClass = FragmentHelp.class;
                            break;
                        default:
                            fragmentClass = FragmentDnewnik.class;
                            break;
                    }
                    Fragment fragmentActiv;

                    try {
                        fragmentActiv = (Fragment) fragmentClass.newInstance();
                        fragmentActiv.setArguments(((MainActivity) context).getBundles.get(position));
                        fragmentManager.beginTransaction().addToBackStack("q").replace(R.id.Smena, fragmentActiv).commit();
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) context).closeDrawer();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public int getItemCount() {
        try {
            return arrayList.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    public void onCheck(int pos, int lastPos) {
        try {
            views.get(pos).setBackgroundColor(Current_Theme.getInt("custom_selected_section", ContextCompat.getColor(context, R.color.custom_selected_section)));
            views.get(lastPos).setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView tittle;
        final LinearLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.menu_icon);
            tittle = itemView.findViewById(R.id.menu_title);
            rootView = itemView.findViewById(R.id.menu_back);

        }
    }
}
