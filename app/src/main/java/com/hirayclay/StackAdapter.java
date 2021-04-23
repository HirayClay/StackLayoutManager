package com.hirayclay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/** Created by CJJ on 2017/3/7. */
public class StackAdapter extends RecyclerView.Adapter<ItemViewHolder> {

  private LayoutInflater inflater;
  private final List<Integer> list;
  public Context context;

  public StackAdapter(List<Integer> list) {
    this.list = list;
  }

  @Override
  @NonNull
  public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (inflater == null) {
      context = parent.getContext();
      inflater = LayoutInflater.from(parent.getContext());
    }
    return new ItemViewHolder(inflater.inflate(R.layout.item_card, parent, false));
  }

  @Override
  public void onBindViewHolder(ItemViewHolder holder, int position) {
    Glide.with(context).load(list.get(position)).into(holder.cover);
    holder.index.setText(list.get(holder.getAdapterPosition()));
  }

  @Override
  public int getItemCount() {
    return list == null ? 0 : list.size();
  }
}
