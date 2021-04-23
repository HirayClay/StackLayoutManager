package com.hirayclay;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

public class TextItemViewHolder extends RecyclerView.ViewHolder {

  ImageView cover;
  TextView index;

  public TextItemViewHolder(View itemView) {
    super(itemView);
    cover = (ImageView) itemView.findViewById(R.id.cover);
    index = (TextView) itemView.findViewById(R.id.index);
    itemView.setOnClickListener(v -> showToast(itemView));
  }

  private void showToast(View itemView) {
    Toast.makeText(itemView.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT)
        .show();
  }
}
