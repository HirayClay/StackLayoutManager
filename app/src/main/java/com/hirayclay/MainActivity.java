package com.hirayclay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ItemChangeListener {
    private static final String TAG = "MainActivity";
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    private List<String> datas;
    private StackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setData();
    }

    @OnClick(R.id.button)
    public void setData() {
        datas = new ArrayList<>();
        datas.add("Item1");
        datas.add("Item2");
        datas.add("Item3");
        datas.add("Item4");
        datas.add("Item5");
        datas.add("Item6");
        datas.add("Item7");
        datas.add("Item8");
        datas.add("Item9");
        datas.add("Item10");
        datas.add("Item11");

        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 4;
        config.initialStackCount = 2;
        config.space = getResources().getDimensionPixelOffset(R.dimen.item_space);
        config.itemSelectedListener = this;
        recyclerview.setLayoutManager(new StackLayoutManager(config));
        recyclerview.setAdapter(adapter = new StackAdapter(datas));

    }

    @OnClick(R.id.button_del)
    public void remove() {
        datas.remove(1);
        adapter.notifyItemRemoved(1);
    }

    @OnClick(R.id.button_add)
    public void insert() {
        datas.add(1, "newItem");
        adapter.notifyItemInserted(1);
    }

    @Override
    public void onItemChange(View itemView, int position) {
        Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
    }
}
