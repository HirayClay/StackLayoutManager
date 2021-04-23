package com.hirayclay;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NestedItemViewHolder extends RecyclerView.ViewHolder {

  RecyclerView nestedRecyclerView;

  public NestedItemViewHolder(View itemView) {
    super(itemView);
    nestedRecyclerView = (RecyclerView) itemView.findViewById(R.id.nested_recyclerview);
  }

  public void bind(ImageItem imageItem) {
    List<Integer> list = imageItem.imageList;
    Config config = new Config();
    config.secondaryScale = 1.0f;
    config.scaleRatio = 0.4f;
    config.maxStackCount = 1;
    config.initialStackCount = 1;
    config.space = 15;
    config.align = Align.LEFT;
    nestedRecyclerView.setLayoutManager(new StackLayoutManager(config));
    nestedRecyclerView.setAdapter(new StackAdapter(list));
  }
}
