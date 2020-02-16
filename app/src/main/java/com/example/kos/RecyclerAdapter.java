package com.example.kos;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList;
  private OnItemClickListener itemClickListener;
  private SharedPreferences Current_Theme;
  private Context context;
  private OnItemLongClickListener itemLongClickListener;

  public void setOnItemClickListener(OnItemClickListener listener){itemClickListener = listener;}
  public void setOnItemLongClickListener(OnItemLongClickListener listener){itemLongClickListener = listener;}

  public interface OnItemClickListener{
    void onItemClick(int position);
  }
  public interface OnItemLongClickListener{
    void onItemLongClick(int position);
  }

  public RecyclerAdapter(ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList, Context context) {
    this.constrRecyclerViewArrayList = constrRecyclerViewArrayList;
      Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
      this.context = context;
  }

  public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView textViewName, textViewBottom;
    public CardView cardView;

    public RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemLongClickListener longClickListener) {
      super(itemView);
      textViewName = itemView.findViewById(R.id.textViewName);
      textViewBottom = itemView.findViewById(R.id.textViewBottom);
      cardView = itemView.findViewById(R.id.card_recycler);

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
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
    RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, itemClickListener, itemLongClickListener);
    return recyclerViewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
    ConstrRecyclerView constrRecyclerView = constrRecyclerViewArrayList.get(position);
    holder.textViewName.setText(constrRecyclerView.getTextName());
    holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
    holder.textViewBottom.setText(constrRecyclerView.getTextBottom());
    holder.textViewBottom.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
    holder.cardView.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
  }


  public void onMove(int firstPos, int secondPos) {
    int fixPos = secondPos-1;
    if(secondPos == 0)
      fixPos = 0;

      ConstrRecyclerView constrRecyclerTemp = constrRecyclerViewArrayList.get(firstPos);
      constrRecyclerViewArrayList.remove(firstPos);
      constrRecyclerViewArrayList.add(fixPos, constrRecyclerTemp);
      notifyItemMoved(firstPos, fixPos);
      notifyItemChanged(fixPos );
  }

  @Override
  public int getItemCount() {
    return constrRecyclerViewArrayList.size();
  }

}