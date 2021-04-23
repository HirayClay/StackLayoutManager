package com.demo.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hirayclay.MainActivity;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

  private final MainActivity mainActivity;
  private final List<StockEntity> dataList;

  public MyAdapter(MainActivity mainActivity, List<StockEntity> dataList) {
    this.mainActivity = mainActivity;
    this.dataList = dataList;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mainActivity).inflate(R.layout.recycler_item, parent, false);
    return new MyViewHolder(mainActivity, view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.bindData(dataList.get(position));
    if (position == dataList.size() - 1) {
      mainActivity.requestHttp();
    }
  }

  @Override
  public int getItemCount() {
    return dataList.size();
  }
}
