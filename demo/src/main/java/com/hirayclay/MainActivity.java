package com.hirayclay;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.demo.demo.MyAdapter;
import com.demo.demo.R;
import com.demo.demo.StockEntity;
import com.demo.demo.Utils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private final List<StockEntity> dataList = new ArrayList<>();
  public int redColor, greenColor;
  public MyAdapter adapter;

  private Handler handler;
  private int currentPage = 0;
  private SwipeRefreshLayout refreshLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 1. 沉浸式状态栏 + dark模式
    View positionView = findViewById(R.id.main_position_view);
    boolean immerse = Utils.immerseStatusBar(this);
    boolean darkMode = Utils.setDarkMode(this);
    if (immerse) {
      ViewGroup.LayoutParams lp = positionView.getLayoutParams();
      lp.height = Utils.getStatusBarHeight(this);
      positionView.setLayoutParams(lp);
      if (!darkMode) {
        positionView.setBackgroundColor(Color.BLACK);
      }
    } else {
      positionView.setVisibility(View.GONE);
    }

    // 2. toolbar
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // 3. recyclerView数据填充
    RecyclerView recyclerView = findViewById(R.id.main_recycler_view);

    Config config = new Config();
    config.secondaryScale = 1.0f;
    config.scaleRatio = 0.4f;
    config.maxStackCount = 1;
    config.initialStackCount = 1;
    config.space = 15;
    config.align = Align.LEFT;
    recyclerView.setLayoutManager(new StackLayoutManager(config));

    adapter = new MyAdapter(this, dataList);
    recyclerView.setAdapter(adapter);

    redColor = getResources().getColor(R.color.red);
    greenColor = getResources().getColor(R.color.green);

    appendDataList();
    adapter.notifyDataSetChanged();

    // 4. refreshLayout
    refreshLayout = findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(this::refresh);
  }

  private void refresh() {
    currentPage = 0;
    requestHttp();
  }

  public void requestHttp() {
    if (null == handler) {
      handler = new Handler(Looper.getMainLooper());
    }
    handler.postDelayed(this::execute, 900);
  }

  private void execute() {
    refreshLayout.setRefreshing(false);
    appendDataList();
    adapter.notifyDataSetChanged();
  }

  private void appendDataList() {
    if (currentPage == 0) {
      dataList.clear();
    }
    currentPage++;

    dataList.add(new StockEntity("Google Inc.", 921.59f, 1, "+6.59 (+0.72%)"));
    dataList.add(new StockEntity("Apple Inc.", 158.73f, 1, "+0.06 (+0.04%)"));
    dataList.add(new StockEntity("Vmware Inc.", 109.74f, -1, "-0.24 (-0.22%)"));
    dataList.add(new StockEntity("Microsoft Inc.", 75.44f, 1, "+0.28 (+0.37%)"));
    dataList.add(new StockEntity("Facebook Inc.", 172.52f, 1, "+2.51 (+1.48%)"));
    dataList.add(new StockEntity("IBM Inc.", 144.40f, -1, "-0.15 (-0.10%)"));
    dataList.add(new StockEntity("Alibaba Inc.", 180.04f, 1, "+0.06 (+0.03%)"));
    dataList.add(new StockEntity("Tencent Inc.", 346.400f, 1, "+2.200 (+0.64%)"));
    dataList.add(new StockEntity("Baidu Inc.", 237.92f, -1, "-1.15 (-0.48%)"));
    dataList.add(new StockEntity("Amazon Inc.", 969.47f, -1, "-4.72 (-0.48%)"));
    dataList.add(new StockEntity("Oracle Inc.", 48.03f, -1, "-0.30 (-0.62%)"));
    dataList.add(new StockEntity("Intel Inc.", 37.22f, 1, "+0.22 (+0.61%)"));
    dataList.add(new StockEntity("Cisco Systems Inc.", 32.49f, -1, "-0.03 (-0.08%)"));
    dataList.add(new StockEntity("Qualcomm Inc.", 52.30f, 1, "+0.05 (+0.10%)"));
    dataList.add(new StockEntity("Sony Inc.", 37.65f, -1, "-0.74 (-1.93%)"));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) { // 点击返回图标事件
      dataList.remove(0);
      adapter.notifyItemRemoved(0);
    }
    return super.onOptionsItemSelected(item);
  }
}
