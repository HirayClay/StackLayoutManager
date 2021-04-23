package com.hirayclay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

/** Created by CJJ on 2017/3/7. */
public class VerticalAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final List<Item> list;
  public Context context;

  public VerticalAdapter(List<Item> list, Context context) {
    this.list = list;
    this.context = context;
  }

  @Override
  @NonNull
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    switch (viewType) {
      case ImageItem.TYPE_IMAGE_LIST:
        return new NestedItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_image_list, parent, false));
      case TextItem.TYPE_TEXT:
        return new TextItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_text, parent, false));
      default:
        throw new RuntimeException();
    }
  }

  @Override
  public int getItemViewType(int position) {
    return list.get(position).type();
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (holder instanceof TextItemViewHolder) {
      ((TextItemViewHolder) holder).bindText(((TextItem) list.get(position)).text);
    } else if (holder instanceof NestedItemViewHolder) {
      ((NestedItemViewHolder) holder).bind(((ImageItem) list.get(position)));
    }
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
}
