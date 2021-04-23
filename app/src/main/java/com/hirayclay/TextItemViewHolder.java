package com.hirayclay;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class TextItemViewHolder extends RecyclerView.ViewHolder {

  TextView tvLabel;

  public TextItemViewHolder(View itemView) {
    super(itemView);
    tvLabel = (TextView) itemView.findViewById(R.id.tv_label);
  }

  public void bindText(String text) {
    tvLabel.setText(text);
  }
}
