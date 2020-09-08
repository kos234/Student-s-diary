package com.example.kos.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.kos.constructors.ConstructorRecyclerView;
import com.example.kos.fragments.FragmentBells;
import com.example.kos.fragments.FragmentTeachers;
import com.example.kos.MainActivity;
import com.example.kos.R;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.RecyclerViewHolder> {
    public final ArrayList<ConstructorRecyclerView> constructorRecyclerViewArrayList;
    private OnItemClickListener itemClickListener, imageClickListener;
    private final SharedPreferences Current_Theme;
    private final SharedPreferences settings;
    private final Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    final int day;

    public void setOnItemClickListener(OnItemClickListener listener, OnItemClickListener imageListener) {
        itemClickListener = listener;
        imageClickListener = imageListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public AdapterRecycler(ArrayList<ConstructorRecyclerView> constructorRecyclerViewArrayList, Context context, int day) {
        this.constructorRecyclerViewArrayList = constructorRecyclerViewArrayList;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        this.context = context;
        this.day = day;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName;
        final TextView textViewBottom;
        final CardView cardView;
        final ImageButton imageButton;
        final SwipeRevealLayout swipeRevealLayout;

        RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemClickListener imageClickListener, final Context context) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBottom = itemView.findViewById(R.id.textViewBottom);
            cardView = itemView.findViewById(R.id.card_recycler);
            imageButton = itemView.findViewById(R.id.hide_button_del);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);

            imageButton.setOnClickListener(view -> {
                try {
                    if (imageClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            imageClickListener.onItemClick(position);
                        }
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            cardView.setOnClickListener(view -> {
                try {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new RecyclerViewHolder(view, itemClickListener, imageClickListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        try {
            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(new Random().nextInt(1000)));
            ConstructorRecyclerView constructorRecyclerView = constructorRecyclerViewArrayList.get(position);
            holder.textViewName.setText(constructorRecyclerView.TextName);
            holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            holder.textViewBottom.setText(constructorRecyclerView.TextBottom);
            holder.textViewBottom.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.cardView.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.imageButton.setBackgroundColor(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)));
            holder.imageButton.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);

            holder.cardView.post(() -> {
                try {
                    holder.imageButton.setLayoutParams(new CardView.LayoutParams(holder.cardView.getHeight(), holder.cardView.getHeight()));
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            holder.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    try {
                        try {
                            FragmentBells fragmentBells = (FragmentBells) ((MainActivity) context).fragmentManager.getFragments().get(0);
                            fragmentBells.setCurrentWindow(new String[]{"null"});
                        } catch (Exception e) {
                            FragmentTeachers fragmentTeachers = (FragmentTeachers) ((MainActivity) context).fragmentManager.getFragments().get(0);
                            fragmentTeachers.setCurrentWindow(new String[]{"null"});
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    try {
                        try {
                            FragmentBells fragmentBells = (FragmentBells) ((MainActivity) context).fragmentManager.getFragments().get(0);
                            fragmentBells.setCurrentWindow(new String[]{"delete", String.valueOf(position), String.valueOf(day)});
                        } catch (Exception e) {
                            FragmentTeachers fragmentTeachers = (FragmentTeachers) ((MainActivity) context).fragmentManager.getFragments().get(0);
                            fragmentTeachers.setCurrentWindow(new String[]{"delete", String.valueOf(position), String.valueOf(day)});
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });
            String[] ids;
            try {
                FragmentBells fragmentBells = (FragmentBells) ((MainActivity) context).fragmentManager.getFragments().get(0);
                ids = fragmentBells.getCurrentWindow();
            } catch (Exception e) {
                FragmentTeachers fragmentTeachers = (FragmentTeachers) ((MainActivity) context).fragmentManager.getFragments().get(0);
                ids = fragmentTeachers.getCurrentWindow();
            }
            if (!ids[0].equals("null"))
                if (ids[0].equals("delete") && ids[1].equals(String.valueOf(position)) && ids[2].equals(String.valueOf(day)))
                    holder.swipeRevealLayout.open(true);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

    }

    public void onMove(int firstPos, int secondPos) {
        try {
            int fixPos = secondPos - 1;
            if (secondPos == 0)
                fixPos = 0;

            ConstructorRecyclerView constrRecyclerTemp = constructorRecyclerViewArrayList.get(firstPos);
            constructorRecyclerViewArrayList.remove(firstPos);
            constructorRecyclerViewArrayList.add(fixPos, constrRecyclerTemp);
            if (settings.getBoolean("AnimationSettings", true)) {
                notifyItemMoved(firstPos, fixPos);
                notifyItemChanged(fixPos);
            } else notifyDataSetChanged();

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

    }

    @Override
    public int getItemCount() {
        try {
            return constructorRecyclerViewArrayList.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }


}