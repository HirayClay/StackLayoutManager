package com.hirayclay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @SuppressLint("NonConstantResourceId")
  @BindView(R.id.recyclerview)
  RecyclerView recyclerview;

  @SuppressLint("NonConstantResourceId")
  @BindView(R.id.button)
  Button button;

  private Unbinder unbinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);
    resetDefault();
  }

  @SuppressLint("NonConstantResourceId")
  @OnClick(R.id.button)
  public void resetDefault() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      list.add(String.valueOf(i));
    }

    Config config = new Config();
    config.secondaryScale = 1.0f;
    config.scaleRatio = 0.4f;
    config.maxStackCount = 1;
    config.initialStackCount = 1;
    config.space = 15;
    config.align = Align.LEFT;
    recyclerview.setLayoutManager(new StackLayoutManager(config));
    recyclerview.setAdapter(new StackAdapter(list));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
