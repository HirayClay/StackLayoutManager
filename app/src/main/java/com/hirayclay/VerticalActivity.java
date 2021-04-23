package com.hirayclay;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;

public class VerticalActivity extends AppCompatActivity {

  @BindView(R.id.recyclerview_vertical)
  RecyclerView verticalRecyclerview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vertical);
    ButterKnife.bind(this);
    vr();
  }

  private void vr() {
    List<String> datas = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      datas.add(String.valueOf(i));
    }

    Config config = new Config();
    config.secondaryScale = 0.95f;
    config.scaleRatio = 0.4f;
    config.maxStackCount = 4;
    config.initialStackCount = 4;
    config.space = 45;
    config.parallex = 1.5f;
    config.align = Align.TOP;
    verticalRecyclerview.setLayoutManager(new StackLayoutManager(config));
    verticalRecyclerview.setAdapter(new StackAdapter(datas).vertical());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.reset:
        vr();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.rest, menu);
    return super.onCreateOptionsMenu(menu);
  }
}
