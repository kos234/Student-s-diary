package com.example.kos;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerThemeAdapter extends RecyclerView.Adapter<RecyclerThemeAdapter.RecyclerViewHolder> {
    private ArrayList<ConstrThemeRecycler> constrRecyclerThemeViewArrayList;
    private RecyclerThemeAdapter.OnItemClickListener itemClickListener;
    private RecyclerThemeAdapter.OnItemLongClickListener itemLongClickListener;
    private RecyclerThemeAdapter.OnCheckedChangeListener onCheckedChangeListener;
    private SharedPreferences settings;
    private Switch aSwitch;

    public void setOnItemClickListener(RecyclerThemeAdapter.OnItemClickListener listener){itemClickListener = listener;}
    public void setOnCheckedChangeListener(RecyclerThemeAdapter.OnCheckedChangeListener onChecked){onCheckedChangeListener = onChecked;}
    public void setOnItemLongClickListener(RecyclerThemeAdapter.OnItemLongClickListener listener){itemLongClickListener = listener;}

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }
    public interface OnCheckedChangeListener{
        void onCheckedChanged(int position, boolean b);
    }

    public RecyclerThemeAdapter(ArrayList<ConstrThemeRecycler> constrRecyclerThemeViewArrayList, Context context) {
        this.constrRecyclerThemeViewArrayList = constrRecyclerThemeViewArrayList;
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName;
        public FrameLayout bolder;
        public ImageView icon;
        public Switch switchTheme;

        public RecyclerViewHolder(@NonNull View itemView, final RecyclerThemeAdapter.OnItemClickListener listener, final RecyclerThemeAdapter.OnItemLongClickListener longClickListener, final RecyclerThemeAdapter.OnCheckedChangeListener checkedChangeListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name_add);
            bolder = itemView.findViewById(R.id.bolder_add);
            icon = itemView.findViewById(R.id.icon_add);
            switchTheme = itemView.findViewById(R.id.switch_add);

            switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkedChangeListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){checkedChangeListener.onCheckedChanged(position, b);}
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){listener.onItemClick(position);}
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(longClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){longClickListener.onItemLongClick(position);}
                    }
                    return false;
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerThemeAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.themes_item,parent,false);
        RecyclerThemeAdapter.RecyclerViewHolder recyclerViewHolder = new RecyclerThemeAdapter.RecyclerViewHolder(view, itemClickListener, itemLongClickListener, onCheckedChangeListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerThemeAdapter.RecyclerViewHolder holder, int position) {
        ConstrThemeRecycler constrRecyclerView = constrRecyclerThemeViewArrayList.get(position);

        holder.textViewName.setText(constrRecyclerView.getName());
        holder.bolder.setBackgroundColor(constrRecyclerView.getColorBolder());
        holder.icon.setBackgroundColor(constrRecyclerView.getColorIcon());
        holder.switchTheme.setId(constrRecyclerView.getIdSwitch());
        aSwitch = holder.switchTheme;
        if(holder.switchTheme.getId() == settings.getInt("id_current_theme", R.id.switchWhite)){
            holder.switchTheme.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return constrRecyclerThemeViewArrayList.size();
    }

    public Switch getSwitch(){
        return aSwitch;
    }

}
