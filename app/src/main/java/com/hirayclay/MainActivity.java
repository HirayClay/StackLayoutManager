package com.hirayclay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        resetDefault();
    }

    @OnClick(R.id.button)
    public void resetDefault() {
        List<String> datas = new ArrayList<>();
        datas.add("1");
        datas.add("2");
        datas.add("3");
        datas.add("4");
        datas.add("5");
        datas.add("6");
        datas.add("7");
        datas.add("8");
        datas.add("9");
        datas.add("10");
        datas.add("11");

        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 4;
        config.initialStackCount = 0;
        config.space = 40;
        config.align = Align.LEFT;
        recyclerview.setLayoutManager(new StackLayoutManager(config));
        recyclerview.setAdapter(new StackAdapter(datas));

    }

    @OnClick(R.id.button1)
    public void resetRight() {
        List<String> datas = new ArrayList<>();
        datas.add("1");
        datas.add("2");
        datas.add("3");
        datas.add("4");
        datas.add("5");
        datas.add("6");
        datas.add("7");
        datas.add("8");
        datas.add("9");
        datas.add("10");
        datas.add("11");

        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 4;
        config.initialStackCount = 0;
        config.space = 40;
        config.align = Align.RIGHT;
        recyclerview.setLayoutManager(new StackLayoutManager(config));
        recyclerview.setAdapter(new StackAdapter(datas));

    }
}
