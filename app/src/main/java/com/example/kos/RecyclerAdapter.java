package com.example.kos;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList;
  private OnItemClickListener itemClickListener;
  private OnItemLongClickListener itemLongClickListener;

  public void setOnItemClickListener(OnItemClickListener listener){itemClickListener = listener;}
  public void setOnItemLongClickListener(OnItemLongClickListener listener){itemLongClickListener = listener;}

  public interface OnItemClickListener{
    void onItemClick(int position);
  }
  public interface OnItemLongClickListener{
    void onItemLongClick(int position);
  }

  public RecyclerAdapter(ArrayList<ConstrRecyclerView> constrRecyclerViewArrayList) {
    this.constrRecyclerViewArrayList = constrRecyclerViewArrayList;
  }

  public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView textViewName, textViewBottom;

    public RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnItemLongClickListener longClickListener) {
      super(itemView);
      textViewName = itemView.findViewById(R.id.textViewName);
      textViewBottom = itemView.findViewById(R.id.textViewBottom);

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
    holder.textViewBottom.setText(constrRecyclerView.getTextBottom());
  }

  @Override
  public int getItemCount() {
    return constrRecyclerViewArrayList.size();
  }

}