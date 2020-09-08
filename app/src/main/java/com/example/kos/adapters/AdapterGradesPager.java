package com.example.kos.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kos.constructors.ConstructorGrades;
import com.example.kos.FixSetTag;
import com.example.kos.fragments.FragmentGrades;
import com.example.kos.MainActivity;
import com.example.kos.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AdapterGradesPager extends RecyclerView.Adapter<AdapterGradesPager.RecyclerViewHolder> {
    final ArrayList<ConstructorGrades> constructorGrades;
    final TextView widthPred;
    final TextView widthOcenka;
    public final Context context;
    final SharedPreferences Current_Theme;
    private OnItemClickListener itemClickListener;
    private final FragmentGrades fragmentGrades;

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v);
    }

    public AdapterGradesPager(ArrayList<ConstructorGrades> constructorGrades, TextView widthPred, TextView widthOcenka, Context context, FragmentGrades fragmentGrades) {
        this.constructorGrades = constructorGrades;
        this.widthPred = widthPred;
        this.widthOcenka = widthOcenka;
        this.context = context;
        this.fragmentGrades = fragmentGrades;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new RecyclerViewHolder(view, itemClickListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        try {
            final ConstructorGrades grades = constructorGrades.get(position);
            widthPred.post(() -> {
                try {
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(widthPred.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMarginEnd(MainActivity.dpSize);
                    holder.linear.setLayoutParams(layoutParams);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            if (constructorGrades.size() - 1 == position)
                holder.tableRow.setPadding(0, 0, 0, 0);

            holder.textViewName.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            holder.textViewOcenka.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewOcenka.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.textViewOcenka.setTag(new FixSetTag().setNUM_DAY(grades.num).setPOSITION_LIST(position));
            holder.textViewOcenka.getHeight();
            widthOcenka.post(() -> {
                try {
                    holder.textViewOcenka.setLayoutParams(new TableRow.LayoutParams(widthOcenka.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT)); //height is ready
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            holder.textViewName.setText(grades.lesionString);
            holder.textViewOcenka.setText(grades.gradesString);
            if (fragmentGrades.currentWindow[0].equals("edit")) {
                if (Integer.parseInt(fragmentGrades.currentWindow[1]) == grades.num && Integer.parseInt(fragmentGrades.currentWindow[2]) == position)
                    fragmentGrades.adapterGrades.onClickEdit(holder.textViewOcenka, position);
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public int getItemCount() {
        try {
            return constructorGrades.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName, textViewOcenka;
        final LinearLayout linear, tableRow;


        RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener, final Context context) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_name_dnev);
            itemView.findViewById(R.id.textView_kab_dnev).setVisibility(View.GONE);
            itemView.findViewById(R.id.textView_dnev_dz).setVisibility(View.GONE);
            textViewOcenka = itemView.findViewById(R.id.textView_dnev_ocenka);
            linear = itemView.findViewById(R.id.linear);
            tableRow = itemView.findViewById(R.id.tableRow);

            textViewOcenka.setOnClickListener(view -> {
                try {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(textViewOcenka);
                        }
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        }
    }

}
