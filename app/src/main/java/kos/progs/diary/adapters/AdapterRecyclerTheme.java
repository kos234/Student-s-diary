package kos.progs.diary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import kos.progs.diary.constructors.ConstructorThemeRecycler;
import kos.progs.diary.fragments.FragmentSettings;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class AdapterRecyclerTheme extends RecyclerView.Adapter<AdapterRecyclerTheme.RecyclerViewHolder> {
    private final ArrayList<ConstructorThemeRecycler> constrRecyclerThemeViewArrayList;
    private OnItemClickListener itemClickListener, imageClickListener;
    private OnCheckedChangeListener onCheckedChangeListener;
    private final SharedPreferences settings;
    private final SharedPreferences Current_Theme;
    private final Context context;
    private SwitchCompat aSwitch;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public void setOnItemClickListener(OnItemClickListener listener, OnItemClickListener imageListener) {
        itemClickListener = listener;
        imageClickListener = imageListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onChecked) {
        onCheckedChangeListener = onChecked;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(int position);
    }

    public AdapterRecyclerTheme(ArrayList<ConstructorThemeRecycler> constrRecyclerThemeViewArrayList, Context context) {
        this.constrRecyclerThemeViewArrayList = constrRecyclerThemeViewArrayList;
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        this.context = context;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ImageButton imageButton;
        final TextView textViewName;
        final FrameLayout bolder;
        final ImageView icon;
        final SwitchCompat switchTheme;
        final SwipeRevealLayout swipeRevealLayout;

        RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemClickListener imageClickListener, final OnCheckedChangeListener checkedChangeListener, final Context context) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_custom_theme);
            textViewName = itemView.findViewById(R.id.name_add);
            bolder = itemView.findViewById(R.id.bolder_add);
            icon = itemView.findViewById(R.id.icon_add);
            imageButton = itemView.findViewById(R.id.hide_button_del);
            switchTheme = itemView.findViewById(R.id.switch_add);
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

            switchTheme.setOnCheckedChangeListener((compoundButton, b) -> {
                try {
                    if (checkedChangeListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            checkedChangeListener.onCheckedChanged(position);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.themes_item, parent, false);
        return new RecyclerViewHolder(view, itemClickListener, imageClickListener, onCheckedChangeListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        try {
            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(new Random().nextInt(1000)));
            ConstructorThemeRecycler constrRecyclerView = constrRecyclerThemeViewArrayList.get(position);
            holder.cardView.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewName.setText(constrRecyclerView.name);
            holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.bolder.setBackgroundColor(constrRecyclerView.ColorBolder);
            holder.icon.setBackgroundColor(constrRecyclerView.ColorIcon);
            holder.switchTheme.setId(constrRecyclerView.IdSwitch);
            FragmentSettings.setSwitchColor(holder.switchTheme, context);
            aSwitch = holder.switchTheme;
            if (holder.switchTheme.getId() == settings.getInt("id_current_theme", 1)) {
                holder.switchTheme.setChecked(true);
            }

            holder.imageButton.setBackgroundColor(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)));
            holder.imageButton.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
            holder.cardView.post(() -> {
                try {
                    holder.imageButton.setLayoutParams(new CardView.LayoutParams(holder.cardView.getHeight(), holder.cardView.getHeight()));
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            final FragmentSettings fragmentSettings = (FragmentSettings) ((MainActivity) context).fragmentManager.getFragments().get(0);
            holder.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    try {
                        if (fragmentSettings.currentWindow[0].equals("delete"))
                            fragmentSettings.currentWindow = new String[]{"null"};
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    try {
                        if (fragmentSettings.currentWindow[0].equals("null"))
                            fragmentSettings.currentWindow = new String[]{"delete", String.valueOf(position)};
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });

            String[] ids = fragmentSettings.currentWindow;
            if (!ids[0].equals("null"))
                if (ids[0].equals("delete") && ids[1].equals(String.valueOf(position)))
                    holder.swipeRevealLayout.open(true);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

    }


    @Override
    public int getItemCount() {
        try {
            return constrRecyclerThemeViewArrayList.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    public SwitchCompat getSwitch() {
        try {
            return aSwitch;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return new SwitchCompat(context);
        }
    }
}