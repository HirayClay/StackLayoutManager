package com.demo.demo;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hirayclay.MainActivity;

class MyViewHolder extends RecyclerView.ViewHolder {

  private final MainActivity mainActivity;
  public TextView nameTv;
  public TextView currentPriceTv;
  public ImageView trendFlagIv;
  public TextView grossTv;

  public MyViewHolder(MainActivity mainActivity, View itemView) {
    super(itemView);
    this.mainActivity = mainActivity;
    this.nameTv = itemView.findViewById(R.id.item_name_tv);
    this.currentPriceTv = itemView.findViewById(R.id.item_current_price);
    this.trendFlagIv = itemView.findViewById(R.id.item_trend_flag);
    this.grossTv = itemView.findViewById(R.id.item_gross);
  }

  @SuppressLint("SetTextI18n")
  public void bindData(StockEntity stockEntity) {
    nameTv.setText(stockEntity.getName());
    currentPriceTv.setText("$" + stockEntity.getPrice());
    trendFlagIv.setImageResource(
        stockEntity.getFlag() > 0 ? R.drawable.up_red : R.drawable.down_green);
    grossTv.setText(stockEntity.getGross());
    grossTv.setTextColor(
        stockEntity.getFlag() > 0 ? mainActivity.redColor : mainActivity.greenColor);
  }
}
