package com.hirayclay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.Arrays;
import java.util.List;

/** Created by CJJ on 2017/3/7. */
public class VerticalAdapter extends RecyclerView.Adapter<ItemViewHolder> {

  private LayoutInflater inflater;
  private final List<String> list;
  public Context context;
  private final List<Integer> imageUrls =
      Arrays.asList(
          R.drawable.xm2,
          R.drawable.xm3,
          R.drawable.xm4,
          R.drawable.xm5,
          R.drawable.xm6,
          R.drawable.xm7,
          R.drawable.xm1,
          R.drawable.xm8,
          R.drawable.xm9,
          R.drawable.xm1,
          R.drawable.xm2,
          R.drawable.xm3,
          R.drawable.xm4,
          R.drawable.xm5,
          R.drawable.xm6);

  public VerticalAdapter(List<String> list) {
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
    Glide.with(context).load(imageUrls.get(position)).into(holder.cover);
    holder.index.setText(list.get(holder.getAdapterPosition()));
  }

  @Override
  public int getItemCount() {
    return list == null ? 0 : list.size();
  }
}
