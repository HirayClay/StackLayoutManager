package com.hirayclay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  @SuppressLint("NonConstantResourceId")
  @BindView(R.id.recyclerview)
  RecyclerView recyclerview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    resetDefault();
  }

  public void resetDefault() {
    recyclerview.setLayoutManager(new LinearLayoutManager(this));
    recyclerview.setAdapter(new VerticalAdapter(ImageDataListUtil.dataList(), this));
  }
}
