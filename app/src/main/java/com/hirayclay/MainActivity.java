package com.hirayclay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  @SuppressLint("NonConstantResourceId")
  @BindView(R.id.recyclerview)
  RecyclerView recyclerview;

  @SuppressLint("NonConstantResourceId")
  @BindView(R.id.button)
  Button button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    resetDefault();
  }

  @SuppressLint("NonConstantResourceId")
  @OnClick(R.id.button)
  public void resetDefault() {
    recyclerview.setLayoutManager(new LinearLayoutManager(this));
    recyclerview.setAdapter(new VerticalAdapter(ImageDataListUtil.dataList(), this));
  }
}
