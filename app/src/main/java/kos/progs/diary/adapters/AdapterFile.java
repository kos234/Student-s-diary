package kos.progs.diary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import kos.progs.diary.constructors.ConstructorFile;
import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.SquareImageView;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AdapterFile extends RecyclerView.Adapter<AdapterFile.RecyclerViewHolder> {

    private final ArrayList<ConstructorFile> constructorFiles;
    private final Context context;
    private final SharedPreferences Current_Theme;
    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(TextView view);
    }

    public AdapterFile(ArrayList<ConstructorFile> constructorFiles, Context context) {
        this.constructorFiles = constructorFiles;
        this.context = context;
        Current_Theme = Objects.requireNonNull(context).getSharedPreferences("Current_Theme", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false), itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFile.RecyclerViewHolder holder, int position) {
        try {
            ConstructorFile file = constructorFiles.get(position);
            Drawable drawable;
            switch (file.type) {
                case "folder":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_folder_24);
                    holder.textViewName.setTag("folder");
                    break;
                case "video":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_videocam_24);
                    break;
                case "photo":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_24);
                    break;
                case "audio":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_audiotrack_24);
                    break;
                case "archive":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_archive_24);
                    holder.textViewName.setTag("archive");
                    break;
                default:
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_insert_drive_file_24);
                    break;
            }
            Objects.requireNonNull(drawable).setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
            holder.imageView.setImageDrawable(drawable);
            holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.textViewName.setText(file.name);
            if (constructorFiles.size() == position + 1)
                holder.border.setVisibility(View.GONE);
            else
                holder.border.setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
        } catch (Exception e) {
            ((MainActivity) context).errorStack(e);
        }
    }

    @Override
    public int getItemCount() {
        return constructorFiles.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName;
        final SquareImageView imageView;
        final View border;

        RecyclerViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textFile);
            textViewName.setTag("");
            imageView = itemView.findViewById(R.id.imageFile);
            border = itemView.findViewById(R.id.border);
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(textViewName);
                    }
                }
            });
        }
    }
}
